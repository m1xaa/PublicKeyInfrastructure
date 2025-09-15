package com.ftnteam11_2025.pki.pki_system.certificates.model;

import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "certificates")
public class CertificateAuthority {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    private String common_name;

    @Column(name="distinguished_name", columnDefinition="TEXT", nullable=false)
    private String distinguishedName;

    @NotNull
    private Date validFrom;

    @NotNull
    private Date validTo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CertificateStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CertificateType type;

    @ManyToOne
    @JoinColumn(name="issuer_id")
    private CertificateAuthority issuer; // null for RootCA

    @NotNull
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;


}
