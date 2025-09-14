package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.mappers.DistinguishedNameMapper.DistinguishedNameMapper;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CertificateAuthority implements ICertificateAuthorityService {

    private final CertificateAuthorityRepository caRepository;
    private final ICertificateGenerator certificateGenerator;
    private final KeyStoreReader keyStoreReader;
    private final KeyStoreWriter keyStoreWriter;
    private final UserRepository userRepository;

    private Optional<User> getUser(Long userId){
        Optional<User> user = userRepository.findById(userId).orElseThrow()
    }

    @Override
    @Transactional
    public com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception {

        // 1. generate key pair
        KeyPair keyPair = CertificateUtils.generateRSAKeyPair();

        // 2. create subject and issuer
        X500Name x500Name = DistinguishedNameMapper.toX500Name(requestDTO);
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
