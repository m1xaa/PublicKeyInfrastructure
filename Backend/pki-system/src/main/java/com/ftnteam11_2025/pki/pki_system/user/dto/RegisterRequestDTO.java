package com.ftnteam11_2025.pki.pki_system.user.dto;

import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import com.ftnteam11_2025.pki.pki_system.user.validation.AllowedRoles;
import com.ftnteam11_2025.pki.pki_system.util.ValidationPatterns;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Organization name is required")
    @Column(nullable = false)
    private String organizationName;

    @NotNull(message = "User role is required")
    private UserRole userRole;

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = ValidationPatterns.EMAIL_REGEX,
            message = "Email must be valid"
    )
    private String email;

    @NotEmpty(message = "Password is required")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_REGEX,
            message = "Password must contain at least 8 characters, at least one letter and one number"
    )
    private String password;

    @AllowedRoles(
            value = {UserRole.REGULAR},
            message = "Only REGULAR role are allowed"
    )
    public UserRole getUserRole() {
        return this.userRole;
    }
}
