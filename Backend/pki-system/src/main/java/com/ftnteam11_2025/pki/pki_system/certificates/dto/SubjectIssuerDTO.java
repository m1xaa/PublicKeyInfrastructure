package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SubjectIssuerDTO {
    private String commonName;
    private String organization;
    private String organizationUnit;
}
