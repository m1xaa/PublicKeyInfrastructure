package com.ftnteam11_2025.pki.pki_system.certificates.mappers.certificate;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mapping(target = "certificateId", source = "id")
    @Mapping(target = "commonName", source = "common_name")
    CertificateResponseDTO toCertificateResponseDTO(CertificateAuthority certificate);
}
