package com.ftnteam11_2025.pki.pki_system.organization.mapper;

import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(source = "name", target = "name")
    Organization toOrganization(OrganizationRequestDTO organizationRequestDTO);

    @Mapping(source = "id", target = "id")
    OrganizationResponseDTO toOrganizationResponseDTO(Organization organization);

    @Mapping(source = "id", target = "id")
    Organization toEntity(OrganizationResponseDTO organizationResponseDTO);
}
