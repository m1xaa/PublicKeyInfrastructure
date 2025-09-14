package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper.DistinguishedNameMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class CertificateAuthority implements ICertificateAuthorityService {

    private final CertificateAuthorityRepository caRepository;
    private final ICertificateGenerator certificateGenerator;
    private final KeyStoreReader keyStoreReader;
    private final KeyStoreWriter keyStoreWriter;
    private final UserRepository userRepository;

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
        saveJKSFile("password", keyPair.getPrivate(), rootCaCert);

        // 6. save certificate to db
        com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority certificateAuthority = new com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority().builder()
                .common_name(requestDTO.getCommonName())
                .distinguishedName(DistinguishedNameMapper.toDistinguishedNameString(x500Name))
                .validFrom(validFrom)
                .validTo(validTo)
                .status(CertificateStatus.Active)
                .type(CertificateType.RootCA)
                .issuer(null)
                .owner(owner)
                .build();

        return caRepository.save(certificateAuthority);
    }

    private void saveJKSFile(String pass, PrivateKey pk, X509Certificate certificate) throws Exception {
        keyStoreWriter.loadKeyStore("src/main/resources/static/keystores/example.jks", pass.toCharArray());
        keyStoreWriter.write("one", pk, pass.toCharArray(),certificate);
        keyStoreWriter.saveKeyStore("src/main/resources/static/keystores/example.jks",   pass.toCharArray());
    }

    public void readJKSFIle(){
        Certificate loadedCertificate = keyStoreReader.readCertificate("src/main/resources/static/keystores/example.jks", "password", "one");
        System.out.println(loadedCertificate);
    }
}
