package com.ftnteam11_2025.pki.pki_system.security.exception;

public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String token, String message) {
        super(message + " Token: " + token);
    }

}
