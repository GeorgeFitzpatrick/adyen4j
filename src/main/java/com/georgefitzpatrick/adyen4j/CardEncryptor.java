package com.georgefitzpatrick.adyen4j;

import com.georgefitzpatrick.adyen4j.exception.EncryptionException;
import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@NoArgsConstructor
public class CardEncryptor {

    private static final String CARD_NUMBER_KEY = "number";
    private static final String EXPIRY_MONTH_KEY = "expiryMonth";
    private static final String EXPIRY_YEAR_KEY = "expiryYear";
    private static final String CVC_KEY = "cvc";
    private static final String HOLDER_NAME_KEY = "holderName";
    private static final String GENERATION_TIME_KEY = "generationtime";

    private static final String BIN_KEY = "binValue";

    static final SimpleDateFormat GENERATION_DATE_FORMAT;

    static {
        GENERATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        GENERATION_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @NonNull
    public static EncryptedCard encryptFields(
            @NonNull final UnencryptedCard unencryptedCard,
            @NonNull final String publicKey
    ) throws EncryptionException {
        try {
            final String formattedGenerationTime =
                    GENERATION_DATE_FORMAT.format(assureGenerationTime(unencryptedCard.getGenerationTime()));
            final ClientSideEncryptor encryptor = new ClientSideEncryptor(publicKey);

            final String encryptedNumber;
            final String encryptedExpiryMonth;
            final String encryptedExpiryYear;
            final String encryptedSecurityCode;

            JsonObject jsonToEncrypt;

            if (unencryptedCard.getNumber() != null) {
                jsonToEncrypt = new JsonObject();
                jsonToEncrypt.addProperty(CARD_NUMBER_KEY, unencryptedCard.getNumber());
                jsonToEncrypt.addProperty(GENERATION_TIME_KEY, formattedGenerationTime);

                encryptedNumber = encryptor.encrypt(jsonToEncrypt.toString());
            } else {
                encryptedNumber = null;
            }

            if (unencryptedCard.getExpiryMonth() != null && unencryptedCard.getExpiryYear() != null) {
                jsonToEncrypt = new JsonObject();
                jsonToEncrypt.addProperty(EXPIRY_MONTH_KEY, unencryptedCard.getExpiryMonth());
                jsonToEncrypt.addProperty(GENERATION_TIME_KEY, formattedGenerationTime);

                encryptedExpiryMonth = encryptor.encrypt(jsonToEncrypt.toString());

                jsonToEncrypt = new JsonObject();
                jsonToEncrypt.addProperty(EXPIRY_YEAR_KEY, unencryptedCard.getExpiryYear());
                jsonToEncrypt.addProperty(GENERATION_TIME_KEY, formattedGenerationTime);

                encryptedExpiryYear = encryptor.encrypt(jsonToEncrypt.toString());
            } else if (unencryptedCard.getExpiryMonth() == null && unencryptedCard.getExpiryYear() == null) {
                encryptedExpiryMonth = null;
                encryptedExpiryYear = null;
            } else {
                throw new EncryptionException("Both expiryMonth and expiryYear need to be set for encryption.", null);
            }

            if (unencryptedCard.getCvc() != null) {
                jsonToEncrypt = new JsonObject();
                jsonToEncrypt.addProperty(CVC_KEY, unencryptedCard.getCvc());
                jsonToEncrypt.addProperty(GENERATION_TIME_KEY, formattedGenerationTime);

                encryptedSecurityCode = encryptor.encrypt(jsonToEncrypt.toString());
            } else {
                encryptedSecurityCode = null;
            }

            return new EncryptedCard(encryptedNumber, encryptedExpiryMonth, encryptedExpiryYear, encryptedSecurityCode);

        } catch (EncryptionException | IllegalStateException e) {
            throw new EncryptionException(e.getMessage() == null ? "No message." : e.getMessage(), e);
        }
    }

    @NonNull
    public static String encrypt(
            @NonNull final UnencryptedCard unencryptedCard,
            @NonNull final  String publicKey
    ) throws EncryptionException {
        final JsonObject cardJson = new JsonObject();

        cardJson.addProperty(CARD_NUMBER_KEY, unencryptedCard.getNumber());
        cardJson.addProperty(EXPIRY_MONTH_KEY, unencryptedCard.getExpiryMonth());
        cardJson.addProperty(EXPIRY_YEAR_KEY, unencryptedCard.getExpiryYear());
        cardJson.addProperty(CVC_KEY, unencryptedCard.getCvc());
        cardJson.addProperty(HOLDER_NAME_KEY, unencryptedCard.getCardHolderName());
        final Date generationTime = assureGenerationTime(unencryptedCard.getGenerationTime());
        cardJson.addProperty(GENERATION_TIME_KEY, GENERATION_DATE_FORMAT.format(generationTime));

        final ClientSideEncryptor encryptor = new ClientSideEncryptor(publicKey);
        return encryptor.encrypt(cardJson.toString());
    }

    @NonNull
    public static String encryptBin(@NonNull String bin, @NonNull String publicKey) throws EncryptionException {
        final JsonObject binJson = new JsonObject();
        binJson.addProperty(BIN_KEY, bin);
        binJson.addProperty(GENERATION_TIME_KEY, GENERATION_DATE_FORMAT.format(assureGenerationTime(new Date())));

        final ClientSideEncryptor encryptor = new ClientSideEncryptor(publicKey);
        return encryptor.encrypt(binJson.toString());
    }

    private static Date assureGenerationTime(@Nullable Date generationTime) {
        if (generationTime == null) {
            return new Date();
        }

        return generationTime;
    }

}
