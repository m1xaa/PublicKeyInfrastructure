package com.ftnteam11_2025.pki.pki_system.util;

import java.security.SecureRandom;
import java.util.Base64;

public class VerificationCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateVerificationCode(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64UrlEncoder.encodeToString(randomBytes);
    }
}
