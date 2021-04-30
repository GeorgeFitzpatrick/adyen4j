package com.georgefitzpatrick.adyen4j;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Getter
public class UnencryptedCard {

    private final String number;
    private final String expiryMonth;
    private final String expiryYear;
    private final String cvc;
    private final String cardHolderName;
    private final Date generationTime;

    public UnencryptedCard(
            @Nullable String number,
            @Nullable String expiryMonth,
            @Nullable String expiryYear,
            @Nullable String cvc,
            @Nullable String cardHolderName,
            @Nullable Date generationTime
    ) {
        this.number = number;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvc = cvc;
        this.cardHolderName = cardHolderName;
        this.generationTime = generationTime;
    }

    public static final class Builder {
        private String number;
        private String expiryMonth;
        private String expiryYear;
        private String cardHolderName;
        private String cvc;
        private Date generationTime;

        @NonNull
        public Builder setNumber(@NonNull String number) {
            this.number = removeWhiteSpaces(number);
            return this;
        }

        @NonNull
        public Builder setExpiryMonth(@NonNull String expiryMonth) {
            this.expiryMonth = removeWhiteSpaces(expiryMonth);
            return this;
        }

        @NonNull
        public Builder setExpiryYear(@NonNull String expiryYear) {
            this.expiryYear = removeWhiteSpaces(expiryYear);
            return this;
        }

        @NonNull
        public Builder setCvc(@NonNull String cvc) {
            this.cvc = removeWhiteSpaces(cvc);
            return this;
        }

        @NonNull
        public Builder setHolderName(@NonNull String holderName) {
            this.cardHolderName = trimAndRemoveMultipleWhiteSpaces(holderName);
            return this;
        }

        @NonNull
        public Builder setGenerationTime(@NonNull Date generationTime) {
            this.generationTime = generationTime;
            return this;
        }

        @NonNull
        public UnencryptedCard build() throws NullPointerException, IllegalStateException {
            require(number == null || number.matches("[0-9]{8,19}"),
                    "number must be null or have 8 to 19 digits (inclusive).");
            require(cardHolderName == null || cardHolderName.length() > 0,
                    "cardHolderName must be null or not empty.");
            require(cvc == null || cvc.matches("[0-9]{3,4}"),
                    "cvc must be null or have 3 to 4 digits.");
            require(expiryMonth == null || this.expiryMonth.matches("0?[1-9]|1[0-2]"),
                    "expiryMonth must be null or between 1 and 12.");
            require(expiryYear == null || expiryYear.matches("20\\d{2}"),
                    "expiryYear must be in the second millennium and first century.");

            return new UnencryptedCard(number, expiryMonth, expiryYear, cvc, cardHolderName, generationTime);
        }

        private String removeWhiteSpaces(String string) {
            return string != null ? string.replaceAll("\\s", "") : null;
        }

        private String trimAndRemoveMultipleWhiteSpaces(String string) {
            return string != null ? string.trim().replaceAll("\\s{2,}", " ") : null;
        }

        private void require(boolean condition, String message) throws IllegalStateException {
            if (!condition) {
                throw new IllegalStateException(message);
            }
        }
    }

}
