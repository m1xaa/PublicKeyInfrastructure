package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRevocationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevocationReason;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevokeCertificateDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateRevocationList;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.ICertificateRevocationListRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateRevocationService;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
    public List<CertificateRevocationResponseDTO> getAll() {
        return certificateRevocationListRepository.findAll().stream()
                .map(crl ->
                        CertificateRevocationResponseDTO.builder()
                                .id(crl.getId())
                                .certificateId(crl.getCertificateAuthority().getId())
                                .revocationReason(crl.getRevocationReason())
                                .generatedAt(crl.getGeneratedAt().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate())
                                .commonName(crl.getCertificateAuthority().getCommon_name())
                                .build()
                ).toList();
    }

    @Override
    public Resource download(UUID id) {
        CertificateRevocationList crl = certificateRevocationListRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Crl not found"));
        Path path = Paths.get("src/main/resources/crl/" + crl.getCertificateAuthority().getSerialNumber() + ".crl");
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            throw new NotFoundError("Certificate failed download");
        }
        return resource;
    }

    @Override
    @Transactional
    public void revokeCertificate(UUID certificateId, RevokeCertificateDTO request) throws Exception {
        CertificateAuthority ca = certificateAuthorityRepository.findById(certificateId)
                .orElseThrow(() -> new NotFoundError("Certificate not found"));

        if (ca.getStatus().equals(CertificateStatus.Revoked)) {
            throw new BadRequestError("Certificate already revoked");
        }

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

        for (CertificateAuthority cer : toRevoke) {
            crlBuilder.addCRLEntry(
                    new BigInteger(cer.getSerialNumber()),
                    new Date(),
                    reason.ordinal()
            );
        }

        PrivateKey issuerPrivateKey =  certificateUtilsService.getIssuer(ca, ca.getOrganization().getName()).getPrivateKey();
        if (issuerPrivateKey == null) {
            throw new BadRequestError("Issuer's private key not found");
        }
        JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        ContentSigner signer = contentSignerBuilder.build(
                issuerPrivateKey
        );

        X509CRLHolder crlHolder = crlBuilder.build(signer);


        Path path = Paths.get("src/main/resources/crl/" + ca.getSerialNumber() + ".crl");
        Files.createDirectories(path.getParent());
        Files.write(path, crlHolder.getEncoded());

        certificateAuthorityRepository.saveAll(toRevoke);

    }

    private void findAllToRevoke(List<CertificateAuthority> root, List<CertificateAuthority> toRevoke) {
        for (CertificateAuthority rootCert: root) {
            if (rootCert.getStatus().equals(CertificateStatus.Revoked)) {
                continue;
            }
            rootCert.setStatus(CertificateStatus.Revoked);
            List<CertificateAuthority> children = certificateAuthorityRepository.findAllByIssuer(rootCert);
            toRevoke.addAll(children);
            findAllToRevoke(children, toRevoke);
        }
    }
}
