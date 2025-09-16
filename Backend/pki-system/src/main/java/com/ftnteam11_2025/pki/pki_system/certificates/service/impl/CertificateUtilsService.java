package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateUtilsService;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
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
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class CertificateUtilsService implements ICertificateUtilsService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final IOrganizationService organizationService;
    private final KeyStoreReader keyStoreReader;
    private final KeyStoreWriter keyStoreWriter;

    @Value("${security.master-key}")
    private String masterKey;

    public CertificateUtilsService(UserRepository userRepository, OrganizationRepository organizationRepository, IOrganizationService organizationService, KeyStoreReader keyStoreReader, KeyStoreWriter keyStoreWriter) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.keyStoreReader = keyStoreReader;
        this.keyStoreWriter = keyStoreWriter;
    }

    @Transactional
    @Override
    public Organization saveTransfer(String organizationName, PrivateKey pk, X509Certificate certificate, CertificateType type, String alias) throws Exception {
        // 1. generate passwords
        String keyStorePassword = PasswordUtils.generateRandomPassword(24);
        String privateKeyPassword = PasswordUtils.generateRandomPassword(24);

        // 2. encrypt passwords
        String encryptedKeyStorePassword = CryptoUtils.encrypt(keyStorePassword, masterKey);
        String encryptedPrivateKeyPassword = CryptoUtils.encrypt(privateKeyPassword, masterKey);

        // 3. create organization
        String ksFilePath = "src/main/resources/static/keystores/" + organizationName + "_" + System.currentTimeMillis() + ".jks";


        Organization organization;
        if(type == CertificateType.RootCA){
            OrganizationRequestDTO organizationRequestDTO = OrganizationRequestDTO.builder()
                    .name(organizationName)
                    .encryptedKeyStorePassword(encryptedKeyStorePassword)
                    .encryptedPrivateKeyPassword(encryptedPrivateKeyPassword)
                    .ksFilePath(ksFilePath)
                    .build();
            organization = organizationService.createOrganization(organizationRequestDTO);
        }else{
            organization = organizationRepository.findByName(organizationName).orElseThrow(() -> new NotFoundError("Organization not found"));
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
            throw new BadRequestError("Invalid organization");
        }
        String alias = certificateAuthority.getAlias();
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
            throw new BadRequestError("Child certificate cannot start before issuer’s validFrom");
        }
        if(validTo.after(certificateAuthority.getValidTo())){
            throw new BadRequestError("Child certificate cannot end after issuer’s validTo");
        }
        if(certificateAuthority.getStatus() != CertificateStatus.Active){
            throw new BadRequestError("Child certificate status must be Active");
        }
    }
}
