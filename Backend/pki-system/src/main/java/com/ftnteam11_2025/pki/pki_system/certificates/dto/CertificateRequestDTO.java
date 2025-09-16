package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CertificateRequestDTO {
    private String commonName;
    private String surname;
    private String givenName;
    private String organization;
    private String organizationalUnit;
    private String country;
    private String email;

    private Long userId;

    private LocalDate validFrom;
    private LocalDate validTo;

    private UUID certificateId;

    private CertificateType certificateType;
    private List<ExtensionDTO> extensions;
}
