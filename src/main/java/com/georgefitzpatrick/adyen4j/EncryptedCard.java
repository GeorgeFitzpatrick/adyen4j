package com.georgefitzpatrick.adyen4j;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class EncryptedCard {

    private final String encryptedCardNumber;
    private final String encryptedExpiryMonth;
    private final String encryptedExpiryYear;
    private final String encryptedSecurityCode;

    EncryptedCard(
            @Nullable String encryptedCardNumber,
            @Nullable String encryptedExpiryMonth,
            @Nullable String encryptedExpiryYear,
            @Nullable String encryptedSecurityCode
    ) {
        this.encryptedCardNumber = encryptedCardNumber;
        this.encryptedExpiryMonth = encryptedExpiryMonth;
        this.encryptedExpiryYear = encryptedExpiryYear;
        this.encryptedSecurityCode = encryptedSecurityCode;
    }

}
