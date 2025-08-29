package com.ftnteam11_2025.pki.pki_system.util.exception;

public class UnauthenticatedError extends RuntimeException {
    public UnauthenticatedError(String message) {
        super(message);
    }
}