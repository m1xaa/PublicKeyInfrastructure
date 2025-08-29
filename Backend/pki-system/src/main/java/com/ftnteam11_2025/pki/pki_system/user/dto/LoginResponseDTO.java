package com.ftnteam11_2025.pki.pki_system.user.dto;

import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import lombok.Data;

import java.time.Instant;

@Data
public class LoginResponseDTO {
    private final Long userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UserRole role;
    private final String jwt;
    private final String refreshToken;
}
