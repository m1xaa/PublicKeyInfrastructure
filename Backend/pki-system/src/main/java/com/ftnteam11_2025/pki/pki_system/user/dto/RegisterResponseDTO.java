package com.ftnteam11_2025.pki.pki_system.user.dto;

import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import lombok.Data;

@Data
public class RegisterResponseDTO {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UserRole userRole;
}
