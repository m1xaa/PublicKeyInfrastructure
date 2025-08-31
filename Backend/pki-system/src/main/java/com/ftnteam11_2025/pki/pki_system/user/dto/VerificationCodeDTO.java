package com.ftnteam11_2025.pki.pki_system.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VerificationCodeDTO {
    @NotEmpty
    private String verificationCode;
}
