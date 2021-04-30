package com.georgefitzpatrick.adyen4j;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.regex.Pattern;

@NoArgsConstructor
public final class ValidationUtils {

    private static final String PUBLIC_KEY_PATTERN = "([A-F]|[0-9]){5}\\|([A-F]|[0-9]){512}";
    private static final int PUBLIC_KEY_SIZE = 5 + 1 + 512;

    public static boolean isPublicKeyValid(@NonNull String publicKey) {
        final Pattern pubKeyPattern = Pattern.compile(PUBLIC_KEY_PATTERN);
        return pubKeyPattern.matcher(publicKey).find() && publicKey.length() == PUBLIC_KEY_SIZE;
    }

}
