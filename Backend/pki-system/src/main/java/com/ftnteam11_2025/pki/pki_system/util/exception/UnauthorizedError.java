package com.ftnteam11_2025.pki.pki_system.util.exception;

public class UnauthorizedError extends RuntimeException {
    public UnauthorizedError() {
    }

    public UnauthorizedError(String message) {
        super(message);
    }
}
