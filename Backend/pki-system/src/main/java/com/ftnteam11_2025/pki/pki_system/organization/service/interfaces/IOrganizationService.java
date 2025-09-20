package com.ftnteam11_2025.pki.pki_system.organization.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.organization.dto.*;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;

import java.util.List;

public interface IOrganizationService {
    Organization saveOrganization(Organization req);
    List<OrganizationHierarchy> getOrganizationHierarchy();
    OrganizationHierarchy getOrganizationHierarchyByOrganization();
    OrganizationResponseDTO create(CreateOrganizationRequestDTO dto);
    List<OrganizationResponseMinDTO> getAll();
}
