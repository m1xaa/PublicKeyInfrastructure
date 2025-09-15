package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper.DistinguishedNameMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
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
import java.util.Date;

@Service
public class CertificateAuthority implements ICertificateAuthorityService {

    @Value("${security.master-key}")
    private String masterKey;

    private final CertificateAuthorityRepository caRepository;
    private final ICertificateGenerator certificateGenerator;
    private final IOrganizationService organizationService;
    private final KeyStoreReader keyStoreReader;
    private final KeyStoreWriter keyStoreWriter;
    private final UserRepository userRepository;

    public CertificateAuthority(CertificateAuthorityRepository caRepository, ICertificateGenerator certificateGenerator, IOrganizationService organizationService, KeyStoreReader keyStoreReader, KeyStoreWriter keyStoreWriter, UserRepository userRepository) {
        this.caRepository = caRepository;
        this.certificateGenerator = certificateGenerator;
        this.organizationService = organizationService;
        this.keyStoreReader = keyStoreReader;
        this.keyStoreWriter = keyStoreWriter;
        this.userRepository = userRepository;
    }

    private User getValidatedUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));
        if(user.getRole()!= UserRole.CA){
            throw new InvalidRequestError("User does not have CA role");
        }
        return user;
    }

    @Override
    @Transactional
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception {
        // 0. validate owner
        User owner = getValidatedUser(requestDTO.getUserId());

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

        // 4. generateRootCa certificate
        X509Certificate rootCaCert = certificateGenerator.generateRootCa(subject.getX500Name(), keyPair, validFrom, validTo, "serialnumber");

        // 5. save to keystore
        Organization organization = saveTransfer(requestDTO.getOrganization(), keyPair.getPrivate(), rootCaCert);

        // 6. save certificate to db
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority.builder()
                .common_name(requestDTO.getCommonName())
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(x500Name))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.RootCA)
                .issuer(null)
                .owner(owner)
                .organization(organization)
                .build();

        return caRepository.save(certificateAuthority);
    }

    private Organization saveTransfer(String name, PrivateKey pk, X509Certificate certificate) throws Exception {
        // 1. generate passwords
        String keyStorePassword = PasswordUtils.generateRandomPassword(24);
        String privateKeyPassword = PasswordUtils.generateRandomPassword(24);

        // 2. encrypt passwords
        String encryptedKeyStorePassword = CryptoUtils.encrypt(keyStorePassword, masterKey);
        String encryptedPrivateKeyPassword = CryptoUtils.encrypt(privateKeyPassword, masterKey);

        // 3. create organization
        String alias = "cert_" + certificate.getSerialNumber();
        String ksFilePath = "src/main/resources/static/keystores/" + name + "_" + System.currentTimeMillis() + ".jks";
        OrganizationRequestDTO organizationRequestDTO = OrganizationRequestDTO.builder()
                .name(name)
                .alias(alias)
                .encryptedKeyStorePassword(encryptedKeyStorePassword)
                .encryptedPrivateKeyPassword(encryptedPrivateKeyPassword)
                .ksFilePath(ksFilePath)
                .build();

        // 4. save jks
        saveJKSFile(keyStorePassword, pk, certificate, ksFilePath, alias);
        return organizationService.createOrganization(organizationRequestDTO);
    }

    private void saveJKSFile(String pass, PrivateKey pk, X509Certificate certificate, String path, String alias) throws Exception {
        keyStoreWriter.loadKeyStore(path, pass.toCharArray());
        keyStoreWriter.write(alias, pk, pass.toCharArray(),certificate);
        keyStoreWriter.saveKeyStore(path,   pass.toCharArray());
    }

    public void readJKSFIle(){
        Certificate loadedCertificate = keyStoreReader.readCertificate("src/main/resources/static/keystores/example.jks", "password", "alias");
        System.out.println(loadedCertificate);
    }
}
