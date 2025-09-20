package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CertificateDetailsDTO {
    private UUID id;
    private SubjectIssuerDTO issuer;
    private SubjectIssuerDTO subject;
    private Date validFrom;
    private Date validTo;
    private String certificateKey;
    private String publicKey;
}
