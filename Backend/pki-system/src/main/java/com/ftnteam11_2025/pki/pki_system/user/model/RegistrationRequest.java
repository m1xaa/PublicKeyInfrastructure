package com.ftnteam11_2025.pki.pki_system.user.model;

import com.ftnteam11_2025.pki.pki_system.util.ValidationPatterns;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "register_requests")
public class RegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String verificationCode;

    @NotNull
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant expirationTime;

    @NotNull(message = "Email is required")
    @Pattern(
            regexp = ValidationPatterns.EMAIL_REGEX,
            message = "Email must be valid"
    )
    @Column(nullable = false)
    private String email;

    @NotNull(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
