package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CertificateSigningRequestDTO(
        UUID caCertificateId,
        LocalDate validTo,
        String commonName,
        String organizationalUnit,
        String country,
        String email

) {
}
