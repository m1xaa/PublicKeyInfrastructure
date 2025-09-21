package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateDetailsDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.SubjectIssuerDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateUtilsService;
import com.ftnteam11_2025.pki.pki_system.organization.dto.CreateOrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.mapper.OrganizationMapper;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.organization.service.impl.OrganizationService;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateUtilsService implements ICertificateUtilsService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final KeyStoreReader keyStoreReader;
    private final KeyStoreWriter keyStoreWriter;
    private final OrganizationMapper organizationMapper;
    private final IOrganizationService organizationService;

    @Value("${security.master-key}")
    private String masterKey;

    public CertificateUtilsService(UserRepository userRepository, OrganizationRepository organizationRepository , KeyStoreReader keyStoreReader, KeyStoreWriter keyStoreWriter, OrganizationMapper organizationMapper, IOrganizationService organizationService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.keyStoreReader = keyStoreReader;
        this.keyStoreWriter = keyStoreWriter;
        this.organizationService = organizationService;
    }

    @Transactional
    @Override
    public Organization saveTransfer(String organizationName, PrivateKey pk, X509Certificate certificate, CertificateType type, String alias, String ksFilePath) throws Exception {
        String keyStorePassword;
        String privateKeyPassword;

        Organization organization;
        if(type == CertificateType.RootCA){
            Optional<Organization> organizationFound = organizationRepository.findByName(organizationName);
            organization = organizationFound.orElseGet(() -> organizationMapper.toEntity(organizationService.create(new CreateOrganizationRequestDTO(organizationName))));
            keyStorePassword = CryptoUtils.decrypt(organization.getEncryptedKeyStorePassword(), masterKey);
            privateKeyPassword = CryptoUtils.decrypt(organization.getEncryptedPrivateKeyPassword(), masterKey);

        }else{
            organization = organizationRepository.findByName(organizationName).orElseThrow(() -> new NotFoundError("Organization not found"));
            keyStorePassword = CryptoUtils.decrypt(organization.getEncryptedKeyStorePassword(), masterKey);
            privateKeyPassword = CryptoUtils.decrypt(organization.getEncryptedPrivateKeyPassword(), masterKey);
        }

        // 4. save jks
        saveJKSFile(keyStorePassword, pk, certificate, ksFilePath, alias, privateKeyPassword);
        return organization;
    }

    @Transactional
    @Override
    public Issuer getIssuer(CertificateAuthority certificateAuthority, String orgName) throws Exception {
        Organization organization = certificateAuthority.getOrganization();
        String name = organization.getName();
        if(!Objects.equals(name, orgName)){
            throw new BadRequestError("Certificate organization and provided organization name are different");
        }
        String alias = certificateAuthority.getAlias();
        String ksFilePath = certificateAuthority.getKsFilePath();
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

    @Transactional
    @Override
    public User validateUser(Long userId, CertificateType requestedType, CertificateType validateType) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        if(requestedType != CertificateType.EndEntity && user.getRole()!= UserRole.CA){
            throw new BadRequestError("User does not have CA role");
        }
        if(requestedType != validateType){
            throw new BadRequestError("Certificate type not supported");
        }
        return user;
    }

    private void saveJKSFile(String pass, PrivateKey pk, X509Certificate certificate, String path, String alias, String pkPass) throws Exception {
        keyStoreWriter.loadKeyStore(path, pass.toCharArray());
        keyStoreWriter.write(alias, pk, pkPass.toCharArray(),certificate);
        keyStoreWriter.saveKeyStore(path, pass.toCharArray());
    }

    @Override
    public void validateRequest(CertificateAuthority certificateAuthority, Date validFrom, Date validTo) throws Exception{
        if(certificateAuthority.getType() != CertificateType.RootCA && certificateAuthority.getType()!= CertificateType.CA){
            throw new BadRequestError("Issuer must be RootCA or CA");
        }
        if(validFrom.before(certificateAuthority.getValidFrom())){
            throw new BadRequestError("Child certificate cannot start before issuer’s start date");
        }
        if(validTo.after(certificateAuthority.getValidTo())){
            throw new BadRequestError("Child certificate cannot end after issuer’s ed date");
        }
        if(certificateAuthority.getStatus() != CertificateStatus.Active){
            throw new BadRequestError("Child certificate status must be Active");
        }
    }

    @Override
    public CertificateDetailsDTO getCertificate(CertificateAuthority cer) throws Exception {
        Organization organization = cer.getOrganization();
        String alias = cer.getAlias();
        String ksFilePath = cer.getKsFilePath();
        String encryptedKSPassword = organization.getEncryptedKeyStorePassword();
        String keyStorePassword = CryptoUtils.decrypt(encryptedKSPassword, masterKey);

        keyStoreWriter.loadKeyStore(ksFilePath, keyStorePassword.toCharArray());
        Certificate certificate = keyStoreReader.readCertificate(ksFilePath, keyStorePassword, alias);
        if(certificate instanceof X509Certificate x509Certificate){
            SubjectIssuerDTO issuer = CertificateUtils.getIssuerOrSubject(x509Certificate, true);
            SubjectIssuerDTO subject = CertificateUtils.getIssuerOrSubject(x509Certificate, false);
            byte[] publicKeyEncoded = x509Certificate.getPublicKey().getEncoded();
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyEncoded);
            return CertificateDetailsDTO.builder()
                    .id(cer.getId())
                    .issuer(issuer)
                    .subject(subject)
                    .validFrom(x509Certificate.getNotBefore())
                    .validTo(x509Certificate.getNotAfter())
                    .certificateKey(CertificateUtils.getSHA256Fingerprint(x509Certificate))
                    .publicKey(publicKeyBase64)
                    .status(cer.getStatus())
                    .build();
        }

        throw new BadRequestError("Fetching certificate failed");

    }
}
