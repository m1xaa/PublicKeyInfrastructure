package com.ftnteam11_2025.pki.pki_system.user.dto;

import com.ftnteam11_2025.pki.pki_system.util.ValidationPatterns;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CASetPasswordRequest {
    @NotEmpty
    private String verificationCode;

    @NotEmpty(message = "Password is required")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_REGEX,
            message = "Password must contain at least 8 characters, at least one letter and one number"
    )
    private String password;
}
