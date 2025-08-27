package com.ftnteam11_2025.pki.pki_system.email.dto;

import lombok.Data;

@Data
public class ActivationEmailBodyDTO {
    private final String firstName;
    private final String lastName;
    private final String activationUrl;
}
