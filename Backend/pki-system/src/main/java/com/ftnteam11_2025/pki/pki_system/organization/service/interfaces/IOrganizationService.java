package com.ftnteam11_2025.pki.pki_system.organization.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;

public interface IOrganizationService {
    public Organization createOrganization(OrganizationRequestDTO req);
}
