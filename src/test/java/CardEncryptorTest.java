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

        String publicKey = "10001|83D0E61238A154C5DE9E5369811B9BAC4E9D34027C981041A016A9607E028D30A225DFD7E2DA1BA405C403A6DD26DBCDC1E6FC086FAF584230B4A9BE29100C9B70B1BAE2AD470DE3CD70E11C2D0A5901F593EC2ED3D20993E5FDED27ED09303E3CACA3575DAB50290896ABD0504370B6764E3B25C7B84B2B455A6522E052E6A81E19FF95D3E230698D57D38EE93100EFB1276C1713345D126B74C2553E828B02C77FEA618EB14AD1FDF8B2CA208BD8FF1A233ACAE0F26BF46DC661AE62C3C7AC831DB1FE77678753BE372B101B2B01E77A4FAE07C3A48A3ECD8AAC0274C2565F85551AD8313277C29CEA1C7B60F9F09CA37308097ABCE5735A66AE0DA66B13CB";

        EncryptedCard encryptedCard = CardEncryptor.encryptFields(card, publicKey);
        System.out.println("encryptedCardNumber: " + encryptedCard.getEncryptedCardNumber());
        System.out.println("encryptedExpiryMonth: " + encryptedCard.getEncryptedExpiryMonth());
        System.out.println("encryptedExpiryYear: " + encryptedCard.getEncryptedExpiryYear());
        System.out.println("encryptedSecurityCode: " + encryptedCard.getEncryptedSecurityCode());
    }

}
