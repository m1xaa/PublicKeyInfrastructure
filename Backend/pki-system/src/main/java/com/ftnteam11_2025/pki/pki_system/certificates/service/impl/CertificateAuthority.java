package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper.DistinguishedNameMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.certificate.CertificateMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.cert.Certificate;
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
    private final IOrganizationService organizationService;
    private final KeyStoreReader keyStoreReader;
    private final KeyStoreWriter keyStoreWriter;
    private final UserRepository userRepository;
    private final CertificateMapper certificateMapper;

    public CertificateAuthority(CertificateAuthorityRepository caRepository, ICertificateGenerator certificateGenerator, IOrganizationService organizationService, KeyStoreReader keyStoreReader, KeyStoreWriter keyStoreWriter, UserRepository userRepository, CertificateAuthorityRepository certificateAuthorityRepository, OrganizationRepository organizationRepository,
                                CertificateMapper certificateMapper) {
        this.caRepository = caRepository;
        this.certificateGenerator = certificateGenerator;
        this.organizationService = organizationService;
        this.keyStoreReader = keyStoreReader;
        this.keyStoreWriter = keyStoreWriter;
        this.userRepository = userRepository;
        this.certificateAuthorityRepository = certificateAuthorityRepository;
        this.organizationRepository = organizationRepository;
        this.certificateMapper = certificateMapper;
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
        User user = validateDTO(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.RootCA, UserRole.CA);

        // 1. generate key pair
        KeyPair keyPair = CertificateUtils.generateRSAKeyPair();

        // 2. create subject and issuer
        X500Name x500Name = DistinguishedNameMapper.buildX500Name(requestDTO.getCommonName(), requestDTO.getSurname(), requestDTO.getGivenName(), requestDTO.getOrganization(), requestDTO.getOrganizationalUnit(),requestDTO.getCountry(), requestDTO.getEmail());
        Subject subject = Subject.builder()
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
        X509Certificate rootCaCert = certificateGenerator.generateRootCa(subject.getX500Name(), keyPair, validFrom, validTo);

        // 5. save to keystore
        Organization organization = saveTransfer(requestDTO.getOrganization(), keyPair.getPrivate(), rootCaCert, CertificateType.RootCA);

        // 6. save certificate to db
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(x500Name))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.RootCA)
                .issuer(null)
                .owner(user)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthority);
    }

    @Transactional
    @Override
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception {
        // 0. validate owner, typeC
        User user = validateDTO(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.CA, UserRole.CA);

        // 1. generate key pair for intermediateCA
        KeyPair keyPair = CertificateUtils.generateRSAKeyPair();

        // 2. create subject and issuer
        X500Name intermediateX500Name = DistinguishedNameMapper.buildX500Name(requestDTO.getCommonName(), requestDTO.getSurname(), requestDTO.getGivenName(), requestDTO.getOrganization(), requestDTO.getOrganizationalUnit(),requestDTO.getCountry(), requestDTO.getEmail());
        Subject subject = Subject.builder()
                .publicKey(keyPair.getPublic())
                .x500Name(intermediateX500Name)
                .build();
        Issuer issuer = getIssuer(requestDTO.getCertificateId(), requestDTO.getOrganization());

        // 3. date
        Date validFrom = Date.from(requestDTO.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date validTo = Date.from(requestDTO.getValidTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        if(validFrom.after(validTo)){
            throw new BadRequestError("Start date must be before end date");
        }

        // 4. create certificate
        X509Certificate caCertificate = certificateGenerator.generateIntermediateCa(subject, issuer, validFrom, validTo);

        // 5. save to key store
        Organization organization = saveTransfer(requestDTO.getOrganization(), keyPair.getPrivate(), caCertificate, CertificateType.CA);

        // 6. DB
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority iss = certificateAuthorityRepository.findById(requestDTO.getCertificateId()).orElseThrow(() -> new NotFoundError("Issuer not found"));

        if(iss.getType() != CertificateType.RootCA && iss.getType()!= CertificateType.CA){
            throw new BadRequestError("Issuer must be RootCA or CA");
        }
        if(validFrom.before(iss.getValidFrom())){
            throw new BadRequestError("Child certificate cannot start before issuer’s validFrom");
        }
        if(validTo.after(iss.getValidTo())){
            throw new BadRequestError("Child certificate cannot end after issuer’s validTo");
        }
        if(iss.getStatus() != CertificateStatus.Active){
            throw new BadRequestError("Child certificate status must be Active");
        }


        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(subject.getX500Name()))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.CA)
                .issuer(iss)
                .owner(user)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthority);

    }

    @Transactional
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createEndEntity(CertificateRequestDTO requestDTO) throws Exception {
        // 0. validacija korisnika i tipa
        User user = validateDTO(requestDTO.getUserId(), requestDTO.getCertificateType(), CertificateType.EndEntity, UserRole.CA);

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
        Issuer issuer = getIssuer(requestDTO.getCertificateId(), requestDTO.getOrganization());

        // 3. date
        Date validFrom = Date.from(requestDTO.getValidFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date validTo = Date.from(requestDTO.getValidTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (validFrom.after(validTo)) {
            throw new BadRequestError("Start date must be before end date");
        }

        // 4. certificate
        X509Certificate endEntityCer = certificateGenerator.generateEndEntity(subject, issuer, validFrom, validTo);

        // 5. organization
        Organization organization = saveTransfer(
                requestDTO.getOrganization(),
                keyPair.getPrivate(),
                endEntityCer,
                CertificateType.EndEntity
        );

        // DB
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority iss = certificateAuthorityRepository.findById(requestDTO.getCertificateId()).orElseThrow(() -> new NotFoundError("Issuer not found"));

        if(iss.getType() != CertificateType.RootCA && iss.getType()!= CertificateType.CA){
            throw new BadRequestError("Issuer must be RootCA or CA");
        }
        if(validFrom.before(iss.getValidFrom())){
            throw new BadRequestError("Child certificate cannot start before issuer’s validFrom");
        }
        if(validTo.after(iss.getValidTo())){
            throw new BadRequestError("Child certificate cannot end after issuer’s validTo");
        }
        if(iss.getStatus() != CertificateStatus.Active){
            throw new BadRequestError("Child certificate status must be Active");
        }

        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(subject.getX500Name()))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.EndEntity)
                .issuer(iss)
                .owner(user)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthority);
    }

    @Transactional
    public Issuer getIssuer(UUID certificateId, String orgName) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = certificateAuthorityRepository.findById(certificateId).orElseThrow(() -> new NotFoundError("RotCA not found"));
        Organization organization = certificateAuthority.getOrganization();
        String name = organization.getName();
        if(!Objects.equals(name, orgName)){
            throw new BadRequestError("Invalid organization");
        }
        String alias = organization.getAlias();
        String ksFilePath = organization.getKsFilePath();
        String encryptedKSPassword = organization.getEncryptedKeyStorePassword();
        String encryptedPKPassword = organization.getEncryptedPrivateKeyPassword();
        Long orgId = organization.getId();

        // decryption
        String keyStorePassword = CryptoUtils.decrypt(encryptedKSPassword, masterKey);
        String privateKeyPassword = CryptoUtils.decrypt(encryptedPKPassword, masterKey);

        // load issuer
        keyStoreWriter.loadKeyStore(ksFilePath, keyStorePassword.toCharArray());
        return keyStoreReader.readIssuerFromStore(ksFilePath, alias, keyStorePassword.toCharArray(), privateKeyPassword.toCharArray());
    }

    private User validateDTO(Long userId, CertificateType type, CertificateType validateType, UserRole validateRole){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        if(user.getRole()!= validateRole){
            throw new BadRequestError("User does not have CA role");
        }

        if(type != validateType){
            throw new BadRequestError("Certificate type not supported, error: creating CA");
        }
        return user;
    }

    @Transactional
    public Organization saveTransfer(String name, PrivateKey pk, X509Certificate certificate, CertificateType type) throws Exception {
        // 1. generate passwords
        String keyStorePassword = PasswordUtils.generateRandomPassword(24);
        String privateKeyPassword = PasswordUtils.generateRandomPassword(24);

        // 2. encrypt passwords
        String encryptedKeyStorePassword = CryptoUtils.encrypt(keyStorePassword, masterKey);
        String encryptedPrivateKeyPassword = CryptoUtils.encrypt(privateKeyPassword, masterKey);

        // 3. create organization
        String alias = "cert_" + certificate.getSerialNumber();
        String ksFilePath = "src/main/resources/static/keystores/" + name + "_" + System.currentTimeMillis() + ".jks";


        Organization organization;
        if(type == CertificateType.RootCA){
            OrganizationRequestDTO organizationRequestDTO = OrganizationRequestDTO.builder()
                    .name(name)
                    .alias(alias)
                    .encryptedKeyStorePassword(encryptedKeyStorePassword)
                    .encryptedPrivateKeyPassword(encryptedPrivateKeyPassword)
                    .ksFilePath(ksFilePath)
                    .build();
            organization = organizationService.createOrganization(organizationRequestDTO);
        }else{
            organization = organizationRepository.findByName(name).orElseThrow(() -> new NotFoundError("Organization not found"));
        }

        // 4. save jks
        saveJKSFile(keyStorePassword, pk, certificate, ksFilePath, alias, privateKeyPassword);
        return organization;
    }

    private void saveJKSFile(String pass, PrivateKey pk, X509Certificate certificate, String path, String alias, String pkPass) throws Exception {
        keyStoreWriter.loadKeyStore(path, pass.toCharArray());
        keyStoreWriter.write(alias, pk, pkPass.toCharArray(),certificate);
        keyStoreWriter.saveKeyStore(path, pass.toCharArray());
    }

    public void readJKSFIle(){
        Certificate loadedCertificate = keyStoreReader.readCertificate("src/main/resources/static/keystores/example.jks", "password", "alias");
        System.out.println(loadedCertificate);
    }

    @Override
    public List<CertificateResponseDTO> getParentCertificate(){
        return certificateAuthorityRepository.findAllByStatusAndTypeNot(CertificateStatus.Active, CertificateType.EndEntity).stream().map(certificateMapper::toCertificateResponseDTO).collect(Collectors.toList());
    }
}
