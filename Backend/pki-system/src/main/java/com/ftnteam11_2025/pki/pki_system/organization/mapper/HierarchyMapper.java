package com.ftnteam11_2025.pki.pki_system.organization.mapper;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationNode;

import java.util.ArrayList;

public class HierarchyMapper {
    public static OrganizationNode toNode(CertificateAuthority c) {
        return OrganizationNode.builder()
                .id(c.getId().toString())
                .commonName(c.getCommon_name())
                .type(c.getType().name())
                .status(c.getStatus().name())
                .validFrom(c.getValidFrom())
                .validTo(c.getValidTo())
                .children(new ArrayList<>())
                .build();
    }
}
