package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.ExtensionDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CertificateGenerator implements ICertificateGenerator {

    // subject == issuer
    @Override
    public X509Certificate generateRootCa(Issuer issuer, Date validFrom, Date validTo, ExtensionDTO extensionDTO) throws Exception {

        X509v3CertificateBuilder certBuilder = createBuilder(issuer.getX500Name(),issuer.getX500Name(), issuer.getPublicKey(),issuer.getPublicKey(), validFrom, validTo, extensionDTO);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuer.getPrivateKey());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }

    @Override
    public X509Certificate generateIntermediateCa(Subject subject, Issuer issuer, Date validFrom, Date validTo, ExtensionDTO extensionDTO) throws Exception {

        X509v3CertificateBuilder certBuilder = createBuilder(subject.getX500Name(), issuer.getX500Name(),subject.getPublicKey(),issuer.getPublicKey(), validFrom, validTo, extensionDTO);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuer.getPrivateKey());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }

    private X509v3CertificateBuilder createBuilder(X500Name subjectName, X500Name issuerName, PublicKey subjectPK, PublicKey issuerPK, Date validFrom, Date validTo, ExtensionDTO extensionDTO) throws CertIOException, NoSuchAlgorithmException {
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName,
                BigInteger.valueOf(System.currentTimeMillis()),
                validFrom,
                validTo,
                subjectName,
                subjectPK
        );

        // extensions
        if (extensionDTO!= null && extensionDTO.getPathLen() != null && extensionDTO.getPathLen() >= 0 && extensionDTO.getPathLen() < 20) {
            certBuilder.addExtension(
                    Extension.basicConstraints,
                    true, // critical
                    new BasicConstraints(extensionDTO.getPathLen())
            );
        } else {
            certBuilder.addExtension(
                    Extension.basicConstraints,
                    true,
                    new BasicConstraints(true) // no limit
            );
        }


        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        if (extensionDTO!= null && extensionDTO.isSubjectKeyIdentifier()) {
            // ----- SKI (subject) -----
            certBuilder.addExtension(
                    Extension.subjectKeyIdentifier,
                    false,
                    extUtils.createSubjectKeyIdentifier(subjectPK)
            );
        }
        if (extensionDTO!= null && extensionDTO.isAuthorityKeyIdentifier()) {
            // ----- AKI (issuer = root) -----
            certBuilder.addExtension(
                    Extension.authorityKeyIdentifier,
                    false,
                    extUtils.createAuthorityKeyIdentifier(issuerPK)
            );

        }

        KeyUsage keyUsage = new KeyUsage(
                KeyUsage.keyCertSign | KeyUsage.cRLSign
        );

        certBuilder.addExtension(
                Extension.keyUsage,
                true,           // critical = true
                keyUsage
        );
        return certBuilder;
    }


    @Override
    public X509Certificate generateEndEntity(Subject subject, Issuer issuer, Date validFrom, Date validTo, ExtensionDTO extensionDTO) throws Exception {
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer.getX500Name(),
                BigInteger.valueOf(System.currentTimeMillis()),
                validFrom,
                validTo,
                subject.getX500Name(),
                subject.getPublicKey()
        );

        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(false)
        );

        if(extensionDTO!= null && (extensionDTO.isServerAuth() || extensionDTO.isClientAuth())) {
            List<KeyPurposeId> eku = new ArrayList<>();
            if (extensionDTO.isServerAuth()) {
                eku.add(KeyPurposeId.id_kp_serverAuth);
            }
            if (extensionDTO.isClientAuth()) {
                eku.add(KeyPurposeId.id_kp_clientAuth);
            }


            if (!eku.isEmpty()) {
                certBuilder.addExtension(
                        Extension.extendedKeyUsage,
                        false,
                        new ExtendedKeyUsage(eku.toArray(new KeyPurposeId[0]))
                );
            }
        }


        // ----- KeyUsage -----
        int keyUsageBits = KeyUsage.digitalSignature | KeyUsage.keyEncipherment;
        certBuilder.addExtension(
                Extension.keyUsage,
                true,
                new KeyUsage(keyUsageBits)
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuer.getPrivateKey());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }
}
