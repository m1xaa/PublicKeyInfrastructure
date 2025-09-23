package com.ftnteam11_2025.pki.pki_system.util.exception;

public class NotFoundError extends RuntimeException {
    public NotFoundError() {
    }
    public NotFoundError(String message) {
        super(message);
    }
}