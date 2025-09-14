package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import org.bouncycastle.asn1.x500.X500Name;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;

public interface ICertificateGenerator {
    public X509Certificate generateRootCa(X500Name subject, KeyPair keyPair, Date validFrom, Date validTo, String serialNumber) throws Exception;
}
