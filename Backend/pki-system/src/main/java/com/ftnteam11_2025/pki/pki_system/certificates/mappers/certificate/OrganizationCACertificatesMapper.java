package com.ftnteam11_2025.pki.pki_system.certificates.mappers.certificate;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.OrganizationCACertificatesResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = CertificateMapper.class)
public interface OrganizationCACertificatesMapper {

    @Mapping(source = "organization.name", target = "organizationName")
    @Mapping(source = "certificates", target = "certificates")
    OrganizationCACertificatesResponseDTO toDto(Organization organization, List<CertificateAuthority> certificates);
}
