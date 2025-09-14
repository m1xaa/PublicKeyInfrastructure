package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateGenerator;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class CertificateGenerator implements ICertificateGenerator {

    // subject == issuer
    @Override
    public X509Certificate generateRootCa(X500Name subject, KeyPair keyPair, Date validFrom, Date validTo, String serialNumber) throws Exception {
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                subject,
                BigInteger.valueOf(System.currentTimeMillis()),
                validFrom,
                validTo,
                subject, // issuer = subject
                keyPair.getPublic()
        );
        // extensions
        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(true) // path length -> to end-entity
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(keyPair.getPrivate());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }
}
