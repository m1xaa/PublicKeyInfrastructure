package com.ftnteam11_2025.pki.pki_system.organization.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.organization.dto.CreateOrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationHierarchy;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;

import java.util.List;

public interface IOrganizationService {
    public Organization saveOrganization(Organization req);
    public List<OrganizationResponseDTO> getAllOrganization();
    public List<OrganizationHierarchy> getOrganizationHierarchy();

    OrganizationResponseDTO create(CreateOrganizationRequestDTO dto);
}
