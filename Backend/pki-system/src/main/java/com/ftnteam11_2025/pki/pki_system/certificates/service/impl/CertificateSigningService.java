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
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Override
    public void createCSRSelfgenerate(Long userId, String caCertificateId, String validTo, MultipartFile pemFile) {
        try {

            String pemContent = new String(pemFile.getBytes());


            PEMParser pemParser = new PEMParser(new StringReader(pemContent));
            Object obj = pemParser.readObject();
            pemParser.close();

            if (!(obj instanceof PKCS10CertificationRequest csr)) {
                throw new BadRequestError("Invalid CSR format");
            }


            X500Name subject = csr.getSubject();

            String commonName = getRdnValue(subject, BCStyle.CN);
            String surname = getRdnValue(subject, BCStyle.SURNAME);
            String givenName = getRdnValue(subject, BCStyle.GIVENNAME);
            String organizationName = getRdnValue(subject, BCStyle.O);
            String organizationalUnit = getRdnValue(subject, BCStyle.OU);
            String country = getRdnValue(subject, BCStyle.C);
            String email = getRdnValue(subject, BCStyle.E);

            User owner = userService.findById(userId);

            CertificateRequestDTO createCertificateRequest = new CertificateRequestDTO(
                    commonName,
                    surname != null ? surname : owner.getLastName(),
                    givenName != null ? givenName : owner.getFirstName(),
                    organizationName != null ? organizationName : owner.getOrganization().getName(),
                    organizationalUnit,
                    country,
                    email,
                    userId,
                    LocalDate.now(),
                    LocalDate.parse(validTo),
                    UUID.fromString(caCertificateId),
                    CertificateType.EndEntity,
                    List.of()
            );

            certificateAuthorityService.createCertificateAuthority(createCertificateRequest);

        } catch (Exception e) {
            throw new BadRequestError("Failed to process CSR: " + e.getMessage());
        }
    }

    private String getRdnValue(X500Name x500Name, ASN1ObjectIdentifier oid) {
        RDN[] rdns = x500Name.getRDNs(oid);
        if (rdns != null && rdns.length > 0) {
            return IETFUtils.valueToString(rdns[0].getFirst().getValue());
        }
        return null;
    }

}

