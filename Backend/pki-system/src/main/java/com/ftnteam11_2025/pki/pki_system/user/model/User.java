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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Organization name is required")
    @Column(nullable = false)
    private String organizationName;

    @NotNull(message = "Role is required")
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Account account;
}
