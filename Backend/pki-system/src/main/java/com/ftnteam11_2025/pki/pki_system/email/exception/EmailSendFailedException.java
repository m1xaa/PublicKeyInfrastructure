package com.ftnteam11_2025.pki.pki_system.email.exception;

public class EmailSendFailedException extends RuntimeException {
    public EmailSendFailedException() {
    }

    public EmailSendFailedException(String message) {
        super(message);
    }
}
