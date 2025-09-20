package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CertificateResponseCard {
    private UUID id;
    private String commonName;
    private String email;
    private Date validFrom;
    private Date validTo;
    private CertificateStatus status;
}
