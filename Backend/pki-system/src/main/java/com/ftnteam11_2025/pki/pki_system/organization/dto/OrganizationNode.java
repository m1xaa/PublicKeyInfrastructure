package com.ftnteam11_2025.pki.pki_system.organization.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationNode {
    private String id;
    private String commonName;
    private String type;
    private String status;
    private Date validFrom;
    private Date validTo;
    private List<OrganizationNode> children = new ArrayList<>();
}
