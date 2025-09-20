package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Builder
public record CertificateRevocationResponseDTO(
        UUID id,
        UUID certificateId,
        String commonName,
        LocalDate generatedAt,
        RevocationReason revocationReason
) {
}
