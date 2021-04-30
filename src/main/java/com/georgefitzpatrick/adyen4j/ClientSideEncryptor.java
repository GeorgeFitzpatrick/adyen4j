package com.georgefitzpatrick.adyen4j;

import com.georgefitzpatrick.adyen4j.exception.EncryptionException;
import lombok.NonNull;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Locale;

public class ClientSideEncryptor {

    private static final String PREFIX = "adyenan";
    private static final String VERSION = "0_1_1";
    private static final String SEPARATOR = "$";

    static {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    private final Cipher aesCipher;
    private final Cipher rsaCipher;
    private final SecureRandom secureRandom;

    public ClientSideEncryptor(@NonNull String publicKeyString) throws EncryptionException {
        if (!ValidationUtils.isPublicKeyValid(publicKeyString)) {
            throw new EncryptionException("Invalid public key: " + publicKeyString, null);
        }

        secureRandom = new SecureRandom();
        final String[] keyComponents = publicKeyString.split("\\|");

        final KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("RSA KeyFactory not found.", e);
        }

        final int radix = 16;
        final RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                new BigInteger(keyComponents[1].toLowerCase(Locale.getDefault()), radix),
                new BigInteger(keyComponents[0].toLowerCase(Locale.getDefault()), radix));

        final PublicKey pubKey;
        try {
            pubKey = keyFactory.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new EncryptionException("Problem reading public key: " + publicKeyString, e);
        }

        try {
            aesCipher = Cipher.getInstance("AES/CCM/NoPadding", "BC");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Problem instantiation AES Cipher Algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException("Problem instantiation AES Cipher Padding", e);
        } catch (NoSuchProviderException e) {
            throw new EncryptionException("Problem instantiation AES Cipher provider", e);
        }

        try {
            rsaCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, pubKey);

        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Problem instantiation RSA Cipher Algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException("Problem instantiation RSA Cipher Padding", e);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Invalid public key: " + publicKeyString, e);
        }
    }

    @NonNull
    public String encrypt(@NonNull String plainText) throws EncryptionException {
        final SecretKey aesKey = generateAesKey();

        final byte[] iv = generateIV();

        final byte[] encrypted;
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
            encrypted = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException("Incorrect AES Block Size", e);
        } catch (BadPaddingException e) {
            throw new EncryptionException("Incorrect AES Padding", e);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Invalid AES Key", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Invalid AES Parameters", e);
        }

        final byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

        final byte[] encryptedAesKey;
        try {
            encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
            return String.format(
                    "%s%s%s%s%s%s",
                    PREFIX,
                    VERSION,
                    SEPARATOR,
                    base64EncodeToString(encryptedAesKey), SEPARATOR,
                    base64EncodeToString(result)
            );
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException("Incorrect RSA Block Size", e);
        } catch (BadPaddingException e) {
            throw new EncryptionException("Incorrect RSA Padding", e);
        }
    }

    private SecretKey generateAesKey() throws EncryptionException {
        final int keySize = 256;
        final KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Unable to get AES algorithm", e);
        }
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    private byte[] generateIV() {
        final int ivSize = 12;
        final byte[] iv = new byte[ivSize];
        secureRandom.nextBytes(iv);
        return iv;
    }

    private String base64EncodeToString(byte[] data) {
        return Base64.getEncoder().withoutPadding().encodeToString(data);
    }

}
