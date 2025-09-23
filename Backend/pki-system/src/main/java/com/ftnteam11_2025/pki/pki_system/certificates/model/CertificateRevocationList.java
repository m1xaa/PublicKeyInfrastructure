package com.ftnteam11_2025.pki.pki_system.certificates.model;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevocationReason;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
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
@Table(name = "certificate_revocation_lists")
public class CertificateRevocationList {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private RevocationReason revocationReason;

    @OneToOne
    @NotNull
    private CertificateAuthority certificateAuthority;

    @NotNull
    @Column(nullable = false)
    private Date generatedAt;
}
