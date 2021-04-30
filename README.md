# adyen4j
Java library to encrypt payment information for use with Adyen's payment API. This library is essentially [Adyen's clientside encryption library for android](https://github.com/Adyen/adyen-android/tree/develop/cse) with couple of changes to get it wokring outside of android. These changes include:
- Adding [Bouncy Castle](https://www.bouncycastle.org/) as a security provider.
- Replacing usage of [android.util.Base64](https://developer.android.com/reference/android/util/Base64) with [java.util.Base64](https://docs.oracle.com/javase/8/docs/api/java/util/Base64.html).
# Encrypting Payment Information
```java
UnencryptedCard card = new UnencryptedCard.Builder()
        .setHolderName("John Doe")
        .setCvc("123")
        .setExpiryMonth("01")
        .setExpiryYear("2030")
        .setGenerationTime(new Date())
        .setNumber("4242424242424242")
        .build();

String publicKey = ""; // you public key

EncryptedCard encryptedCard = CardEncryptor.encryptFields(card, publicKey);
```
The above example shows you how to create a new UnencryptedCard and then how to encrypt it, creating an EncryptedCard, using your public key provided by Adyen. An EncyrptedCard stores the encrypted card number, encrypted expiry month, encrypted expiry year and the encrypted security code, which are all the required fields you need to submit the payment information to adyen.
