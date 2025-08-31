package com.ftnteam11_2025.pki.pki_system.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDTO {
    private String recipientEmail;
    private String subject;
    private String body;
}
