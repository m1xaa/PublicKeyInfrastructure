package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateResponseDTO {
    private UUID certificateId;
    private String commonName;
}
