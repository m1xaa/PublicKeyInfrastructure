package com.ftnteam11_2025.pki.pki_system.user.model;

import com.ftnteam11_2025.pki.pki_system.util.ValidationPatterns;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Email is required")
    @Pattern(
            regexp = ValidationPatterns.EMAIL_REGEX,
            message = "Email must be valid"
    )
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Account status is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private User user;
}