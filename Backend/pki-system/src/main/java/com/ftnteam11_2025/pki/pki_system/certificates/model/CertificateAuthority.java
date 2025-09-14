package com.ftnteam11_2025.pki.pki_system.certificates.model;

import com.ftnteam11_2025.pki.pki_system.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotNull
    private String common_name;

    @Column(name="distinguished_name", columnDefinition="TEXT", nullable=false)
    private String distinguishedName;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validTo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CertificateStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CertificateType type;

    @ManyToOne
    private CertificateAuthority issuer; // null for RootCA

    @NotNull
    @ManyToOne
    private User owner;

}
