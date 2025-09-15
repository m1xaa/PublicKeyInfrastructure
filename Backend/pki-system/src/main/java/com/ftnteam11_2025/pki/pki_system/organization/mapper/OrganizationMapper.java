package com.ftnteam11_2025.pki.pki_system.organization.mapper;

import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    Organization toOrganization(OrganizationRequestDTO organizationRequestDTO);
}
