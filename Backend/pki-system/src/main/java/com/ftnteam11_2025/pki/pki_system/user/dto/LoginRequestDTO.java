package com.ftnteam11_2025.pki.pki_system.user.dto;

import com.ftnteam11_2025.pki.pki_system.util.ValidationPatterns;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotNull(message = "Email is required")
    @Pattern(
            regexp = ValidationPatterns.EMAIL_REGEX,
            message = "Email must be valid"
    )
    private String email;

    @NotNull(message = "Password is required")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_REGEX,
            message = "Password must contain at least 8 characters, at least one letter and one number"
    )
    private String password;
}