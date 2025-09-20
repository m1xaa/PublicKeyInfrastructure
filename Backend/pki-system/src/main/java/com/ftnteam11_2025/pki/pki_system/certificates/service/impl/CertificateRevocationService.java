package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevocationReason;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevokeCertificateDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateRevocationList;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.ICertificateRevocationListRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateRevocationService;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateRevocationService implements ICertificateRevocationService {

    private final CertificateAuthorityRepository certificateAuthorityRepository;
    private final ICertificateRevocationListRepository certificateRevocationListRepository;
    private final CertificateUtilsService certificateUtilsService;

    @Override
    @Transactional
    public void revokeCertificate(UUID certificateId, RevokeCertificateDTO request) throws Exception {
        CertificateAuthority ca = certificateAuthorityRepository.findById(certificateId)
                .orElseThrow(() -> new NotFoundError("Certificate not found"));

        CertificateRevocationList crl = certificateRevocationListRepository.findByCertificateAuthority(ca)
                .orElse(CertificateRevocationList.builder()
                        .certificateAuthority(ca)
                        .generatedAt(new Date())
                        .revocationReason(request.reason())
                        .build());

        certificateRevocationListRepository.save(crl);

        generateCrlFile(ca, request.reason());
    }

    private void generateCrlFile(CertificateAuthority ca, RevocationReason reason) throws Exception {
        ArrayList<CertificateAuthority> toRevoke = new ArrayList<>(List.of(ca));
        ArrayList<CertificateAuthority> root = new ArrayList<>(List.of(ca));
        findAllToRevoke(root, toRevoke);

        X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(
                new X500Name(ca.getDistinguishedName()),
                new Date()
        );

        Date nextUpdate = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
        crlBuilder.setNextUpdate(nextUpdate);

        for (CertificateAuthority cr : toRevoke) {
            crlBuilder.addCRLEntry(
                    new BigInteger(ca.getSerialNumber()),
                    new Date(),
                    reason.ordinal()
            );
        }
        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = contentSignerBuilder.build(
                certificateUtilsService.getIssuer(ca, ca.getOrganization().getName()).getPrivateKey()
        );

        X509CRLHolder crlHolder = crlBuilder.build(signer);


        Path path = Paths.get("src/main/resources/crl/" + ca.getSerialNumber() + ".crl");
        Files.createDirectories(path.getParent());
        Files.write(path, crlHolder.getEncoded());

        certificateAuthorityRepository.saveAll(toRevoke);

    }

    private void findAllToRevoke(List<CertificateAuthority> root, List<CertificateAuthority> toRevoke) {
        for (CertificateAuthority rootCert: root) {
            rootCert.setStatus(CertificateStatus.Revoked);
            List<CertificateAuthority> children = certificateAuthorityRepository.findAllByIssuer(rootCert);
            toRevoke.addAll(children);
            findAllToRevoke(children, toRevoke);
        }
    }
}
