    package com.ftnteam11_2025.pki.pki_system.user.model;

    import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
    import com.ftnteam11_2025.pki.pki_system.security.refresh.model.RefreshToken;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import lombok.*;

    import java.util.List;

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

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private Organization organization;

        @NotNull(message = "Role is required")
        @Column(nullable = false)
        private UserRole role;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(unique = true)
        private Account account;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private List<RefreshToken>  refreshTokens;
    }
