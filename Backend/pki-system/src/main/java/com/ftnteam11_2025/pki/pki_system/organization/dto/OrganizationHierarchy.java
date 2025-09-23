package com.ftnteam11_2025.pki.pki_system.organization.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationHierarchy {
    private long organizationId;
    private String organizationName;
    private List<OrganizationNode> rootCertificates = new ArrayList<>();
}
