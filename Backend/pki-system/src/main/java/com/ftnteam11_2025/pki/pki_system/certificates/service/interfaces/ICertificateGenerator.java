package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.ExtensionDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Subject;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

public interface ICertificateGenerator {
    X509Certificate generateRootCa(Issuer issuer, Date validFrom, Date validTo, ExtensionDTO extReq) throws Exception;
    public X509Certificate generateIntermediateCa(
            Subject subject, Issuer issuer, Date validFrom, Date validTo,ExtensionDTO extReq
    ) throws Exception;
    public X509Certificate generateEndEntity(Subject subject, Issuer issuer, Date validFrom, Date validTo,ExtensionDTO extReq) throws Exception;
}
