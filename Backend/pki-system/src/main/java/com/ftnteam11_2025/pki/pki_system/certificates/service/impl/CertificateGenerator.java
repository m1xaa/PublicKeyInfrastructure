package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
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
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class CertificateGenerator implements ICertificateGenerator {

    // subject == issuer
    @Override
    public X509Certificate generateRootCa(Issuer issuer, Date validFrom, Date validTo) throws Exception {
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer.getX500Name(),
                BigInteger.valueOf(System.currentTimeMillis()),
                validFrom,
                validTo,
                issuer.getX500Name(), // issuer = subject
                issuer.getPublicKey()
        );
        // extensions
        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(true) // path length -> to end-entity
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuer.getPrivateKey());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }

    @Override
    public X509Certificate generateIntermediateCa(Subject subject, Issuer issuer, Date validFrom, Date validTo) throws Exception {
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
                new BasicConstraints(true) // path length -> to end-entity
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuer.getPrivateKey());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }

    @Override
    public X509Certificate generateEndEntity(Subject subject, Issuer issuer, Date validFrom, Date validTo) throws Exception {
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

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(issuer.getPrivateKey());

        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(signer));
    }
}
