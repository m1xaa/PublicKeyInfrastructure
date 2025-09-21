package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.*;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper.DistinguishedNameMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.certificate.CertificateMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateUtilsService;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.user.service.AuthService;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateAuthority implements ICertificateAuthorityService {

    private final CertificateAuthorityRepository certificateAuthorityRepository;
    private final OrganizationRepository organizationRepository;
    @Value("${security.master-key}")
    private String masterKey;

    private final CertificateAuthorityRepository caRepository;
    private final ICertificateGenerator certificateGenerator;
    private final CertificateMapper certificateMapper;
    private final ICertificateUtilsService certificateUtilsService;
    private final AuthService authService;

    public CertificateAuthority(CertificateAuthorityRepository caRepository, ICertificateGenerator certificateGenerator, CertificateAuthorityRepository certificateAuthorityRepository, OrganizationRepository organizationRepository,
                                CertificateMapper certificateMapper, ICertificateUtilsService certificateUtilsService, AuthService authService) {
        this.caRepository = caRepository;
        this.certificateGenerator = certificateGenerator;
        this.certificateAuthorityRepository = certificateAuthorityRepository;
        this.certificateMapper = certificateMapper;
        this.certificateUtilsService = certificateUtilsService;
        this.authService = authService;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public CertificateResponseDTO createCertificateAuthority(CertificateRequestDTO req) throws Exception {
        if(req.getCertificateType() == CertificateType.RootCA){
            return certificateMapper.toCertificateResponseDTO(createRootCA(req));
        }
        else if(req.getCertificateType() == CertificateType.CA){
            return certificateMapper.toCertificateResponseDTO(createCA(req));
        }
        else{
            return certificateMapper.toCertificateResponseDTO(createEndEntity(req));
        }
    }

    @Override
    @Transactional
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createRootCA(CertificateRequestDTO requestDTO) throws Exception {
        // 0. validate owner, typeC
        User user = certificateUtilsService.validateUser(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.RootCA);
        if(!Objects.equals(user.getOrganization().getName(), requestDTO.getOrganization())){
            throw new BadRequestError("User organization and provided organization do not match");
        }
        // 1. generate key pair
        KeyPair keyPair = CertificateUtils.generateRSAKeyPair();


        // 2. create subject and issuer
        X500Name x500Name = DistinguishedNameMapper.buildX500Name(requestDTO.getCommonName(), requestDTO.getSurname(), requestDTO.getGivenName(), requestDTO.getOrganization(), requestDTO.getOrganizationalUnit(),requestDTO.getCountry(), requestDTO.getEmail());
        Issuer issuer = Issuer.builder()
                .privateKey(keyPair.getPrivate())
                .publicKey(keyPair.getPublic())
                .x500Name(x500Name)
                .build();

        // 3. preprocessing attr
        Date validFrom = Date.from(requestDTO.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date validTo = Date.from(requestDTO.getValidTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        if(validFrom.after(validTo)){
            throw new BadRequestError("Start date must be before end date");
        }

        // 4. generateRootCa certificate
        X509Certificate rootCaCert = certificateGenerator.generateRootCa(issuer, validFrom, validTo, requestDTO.getExtensions());
        String alias = "cert_" + rootCaCert.getSerialNumber();
        String ksFilePath = "src/main/resources/static/keystores/" + System.currentTimeMillis() + ".jks";

        // 5. save to keystore
        Organization organization = certificateUtilsService.saveTransfer(requestDTO.getOrganization(), keyPair.getPrivate(), rootCaCert, CertificateType.RootCA, alias, ksFilePath);

        // 6. save certificate to db
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .serialNumber(String.valueOf(rootCaCert.getSerialNumber()))
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(x500Name))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.RootCA)
                .issuer(null)
                .owner(user)
                .alias(alias)
                .ksFilePath(ksFilePath)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthority);
    }

    @Transactional
    @Override
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception {

        // 0. validate owner, typeC
        User user = certificateUtilsService.validateUser(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.CA);

        // 1. generate key pair for intermediateCA
        KeyPair keyPair = CertificateUtils.generateRSAKeyPair();

        // 2. create subject and issuer
        X500Name intermediateX500Name = DistinguishedNameMapper.buildX500Name(requestDTO.getCommonName(), requestDTO.getSurname(), requestDTO.getGivenName(), requestDTO.getOrganization(), requestDTO.getOrganizationalUnit(),requestDTO.getCountry(), requestDTO.getEmail());
        Subject subject = Subject.builder()
                .publicKey(keyPair.getPublic())
                .x500Name(intermediateX500Name)
                .build();
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = certificateAuthorityRepository.findById(requestDTO.getCertificateId()).orElseThrow(() -> new NotFoundError("RotCA not found"));
        Issuer issuer = certificateUtilsService.getIssuer(certificateAuthority, requestDTO.getOrganization());

        // 3. date
        Date validFrom = Date.from(requestDTO.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date validTo = Date.from(requestDTO.getValidTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        if(validFrom.after(validTo)){
            throw new BadRequestError("Start date must be before end date");
        }

        // validation
        certificateUtilsService.validateRequest(certificateAuthority, validFrom, validTo);

        // 4. create certificate
        X509Certificate caCertificate = certificateGenerator.generateIntermediateCa(subject, issuer, validFrom, validTo, requestDTO.getExtensions());
        String alias = "cert_" + caCertificate.getSerialNumber();
        String ksFilePath = "src/main/resources/static/keystores/" + System.currentTimeMillis() + ".jks";

        // 5. save to key store
        Organization organization = certificateUtilsService.saveTransfer(requestDTO.getOrganization(), keyPair.getPrivate(), caCertificate, CertificateType.CA, alias, ksFilePath);

        // 6. DB
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthoritySave = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .serialNumber(String.valueOf(caCertificate.getSerialNumber()))
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(subject.getX500Name()))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.CA)
                .issuer(certificateAuthority)
                .owner(user)
                .alias(alias)
                .ksFilePath(ksFilePath)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthoritySave);

    }

    @Transactional
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createEndEntity(CertificateRequestDTO requestDTO) throws Exception {
        // 0. validacija korisnika i tipa
        User user = certificateUtilsService.validateUser(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.EndEntity);

        // 1. key pair za krajnji entitet
        KeyPair keyPair = CertificateUtils.generateRSAKeyPair();

        // 2. subject, issuer
        X500Name endEntityX500Name = DistinguishedNameMapper.buildX500Name(
                requestDTO.getCommonName(),
                requestDTO.getSurname(),
                requestDTO.getGivenName(),
                requestDTO.getOrganization(),
                requestDTO.getOrganizationalUnit(),
                requestDTO.getCountry(),
                requestDTO.getEmail()
        );
        Subject subject = Subject.builder()
                .publicKey(keyPair.getPublic())
                .x500Name(endEntityX500Name)
                .build();
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority iss = certificateAuthorityRepository.findById(requestDTO.getCertificateId()).orElseThrow(() -> new NotFoundError("Issuer not found"));

        Issuer issuer = certificateUtilsService.getIssuer(iss, requestDTO.getOrganization());

        // 3. date
        Date validFrom = Date.from(requestDTO.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date validTo = Date.from(requestDTO.getValidTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (validFrom.after(validTo)) {
            throw new BadRequestError("Start date must be before end date");
        }

        // validation
        certificateUtilsService.validateRequest(iss, validFrom, validTo);

        // 4. certificate
        X509Certificate endEntityCer = certificateGenerator.generateEndEntity(subject, issuer, validFrom, validTo, requestDTO.getExtensions());
        String alias = "cert_" + endEntityCer.getSerialNumber();
        String ksFilePath = "src/main/resources/static/keystores/" + System.currentTimeMillis() + ".jks";
        // 5. organization
        Organization organization = certificateUtilsService.saveTransfer(
                requestDTO.getOrganization(),
                keyPair.getPrivate(),
                endEntityCer,
                CertificateType.EndEntity,
                alias,
                ksFilePath
        );

        // 6. DB
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .serialNumber(String.valueOf(endEntityCer.getSerialNumber()))
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(subject.getX500Name()))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.EndEntity)
                .issuer(iss)
                .owner(user)
                .alias(alias)
                .ksFilePath(ksFilePath)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthority);
    }

    @Transactional
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createEndEntityFromCSR(CertificateRequestDTO requestDTO, PublicKey publicKey) throws Exception {
        User user = certificateUtilsService.validateUser(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.EndEntity);

        // Subject with CSR public key
        X500Name endEntityX500Name = DistinguishedNameMapper.buildX500Name(
                requestDTO.getCommonName(),
                requestDTO.getSurname(),
                requestDTO.getGivenName(),
                requestDTO.getOrganization(),
                requestDTO.getOrganizationalUnit(),
                requestDTO.getCountry(),
                requestDTO.getEmail()
        );

        Subject subject = Subject.builder()
                .publicKey(publicKey)
                .x500Name(endEntityX500Name)
                .build();

        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority issuerCA = certificateAuthorityRepository.findById(requestDTO.getCertificateId())
                .orElseThrow(() -> new NotFoundError("Issuer not found"));

        Issuer issuer = certificateUtilsService.getIssuer(issuerCA, requestDTO.getOrganization());

        // Dates
        Date validFrom = Date.from(requestDTO.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date validTo = Date.from(requestDTO.getValidTo().atStartOfDay(ZoneId.systemDefault()).toInstant());

        certificateUtilsService.validateRequest(issuerCA, validFrom, validTo);

        // Generate cert signed by CA
        X509Certificate endEntityCert = certificateGenerator.generateEndEntity(subject, issuer, validFrom, validTo);

        String alias = "cert_" + endEntityCert.getSerialNumber();
        String ksFilePath = "src/main/resources/static/keystores/" + System.currentTimeMillis() + ".jks";

        Organization organization = certificateUtilsService.saveTransfer(
                requestDTO.getOrganization(),
                null,
                endEntityCert,
                CertificateType.EndEntity,
                alias,
                ksFilePath
        );

        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certAuth =  com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .serialNumber(String.valueOf(endEntityCert.getSerialNumber()))
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(subject.getX500Name()))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.EndEntity)
                .issuer(issuerCA)
                .owner(user)
                .alias(alias)
                .ksFilePath(ksFilePath)
                .organization(organization)
                .build();

        return certificateAuthorityRepository.save(certAuth);
    }


    @Override
    public List<CertificateResponseDTO> getParentCertificate(){
        return certificateAuthorityRepository.findAllByStatusAndTypeNot(CertificateStatus.Active, CertificateType.EndEntity).stream().map(certificateMapper::toCertificateResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<CertificateResponseDTO> getParentCertificateByOrganization(String name) {
        Organization organization = organizationRepository.findByName(name).orElseThrow(()-> new BadRequestError("Organization not found"));
        return certificateAuthorityRepository.findAllByStatusAndTypeNotAndOrganization(CertificateStatus.Active, CertificateType.EndEntity, organization).stream().map(certificateMapper::toCertificateResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Resource downloadCertificateAuthority(UUID id) throws Exception {
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = certificateAuthorityRepository.findById(id).orElseThrow(()-> new BadRequestError("Certificate not found"));
        Path path = Paths.get(certificateAuthority.getKsFilePath());
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            throw new NotFoundError("Certificate failed download");
        }
        return resource;
    }

    @Override
    public List<CertificateResponseCard> getCertificates() {
        List<CertificateResponseCard> res = new ArrayList<>();
        List<com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority> resEntity =
                certificateAuthorityRepository.findAll();

        for (com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority cer : resEntity) {
            UUID issuerId = (cer.getIssuer() != null) ? cer.getIssuer().getId() : null;

            CertificateResponseCard item = CertificateResponseCard.builder()
                    .id(cer.getId())
                    .issuerId(issuerId)
                    .email(cer.getOwner().getAccount().getEmail())
                    .validFrom(cer.getValidFrom())
                    .validTo(cer.getValidTo())
                    .status(cer.getStatus())
                    .commonName(cer.getCommon_name())
                    .build();

            res.add(item);
        }
        return res;
    }


    @Override
    public CertificateDetailsDTO getCertificateDetails(UUID id) throws Exception {
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority cer = certificateAuthorityRepository.findById(id).orElseThrow(()-> new BadRequestError("Certificate not found"));
        return certificateUtilsService.getCertificate(cer);
    }
}
