package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateSigningRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.OrganizationCACertificatesResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.certificate.OrganizationCACertificatesMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateSigningService;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.service.UserService;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateSigningService implements ICertificateSigningService {

    private final UserService userService;
    private final ICertificateAuthorityService certificateAuthorityService;
    private final CertificateAuthorityRepository certificateAuthorityRepository;
    private final OrganizationCACertificatesMapper organizationCACertificatesMapper;

    public OrganizationCACertificatesResponseDTO getOrganizationCACertificates(Long userId) {
        Organization organization = userService.findOrganizationByUserId(userId);
        List<CertificateAuthority> certificates =
                certificateAuthorityRepository.findAllByStatusAndTypeNotAndOrganization(
                        CertificateStatus.Active,
                        CertificateType.EndEntity,
                        organization
                );

        return organizationCACertificatesMapper.toDto(organization, certificates);
    }

    @Override
    public void createCSRAutogenerate(Long userId, CertificateSigningRequestDTO request) throws Exception {
        User owner = userService.findById(userId);
        CertificateRequestDTO createCertificateRequest = new CertificateRequestDTO(
                request.commonName(),
                owner.getLastName(),
                owner.getFirstName(),
                owner.getOrganization().getName(),
                request.organizationalUnit(),
                request.country(),
                request.email(),
                userId,
                LocalDate.now(),
                request.validTo(),
                request.caCertificateId(),
                CertificateType.EndEntity,
                List.of()
        );
        certificateAuthorityService.createCertificateAuthority(createCertificateRequest);

    }
}

