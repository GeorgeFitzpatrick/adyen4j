import com.georgefitzpatrick.adyen4j.CardEncryptor;
import com.georgefitzpatrick.adyen4j.EncryptedCard;
import com.georgefitzpatrick.adyen4j.UnencryptedCard;
import com.georgefitzpatrick.adyen4j.exception.EncryptionException;
import org.junit.Test;

import java.util.Date;

public class CardEncryptorTest {

    @Test
    public void testCardEncryptor() throws EncryptionException {
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
        System.out.println("encryptedCardNumber: " + encryptedCard.getEncryptedCardNumber());
        System.out.println("encryptedExpiryMonth: " + encryptedCard.getEncryptedExpiryMonth());
        System.out.println("encryptedExpiryYear: " + encryptedCard.getEncryptedExpiryYear());
        System.out.println("encryptedSecurityCode: " + encryptedCard.getEncryptedSecurityCode());
    }

}
