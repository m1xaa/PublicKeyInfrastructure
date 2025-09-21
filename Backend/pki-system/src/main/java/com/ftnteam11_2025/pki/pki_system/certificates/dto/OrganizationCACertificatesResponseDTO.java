package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import java.util.List;

public record OrganizationCACertificatesResponseDTO(
        String organizationName,
        List<CertificateResponseDTO> certificates
) {
}
