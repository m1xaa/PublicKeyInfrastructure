package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateDetailsDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.certificates.model.Issuer;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.user.model.User;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.UUID;

public interface ICertificateUtilsService {
    Organization saveTransfer(String organizationName,
                              PrivateKey pk,
                              X509Certificate certificate,
                              CertificateType type,
                              String alias,
                              String ksFilePath) throws Exception;

    Issuer getIssuer(CertificateAuthority certificateAuthority, String orgName) throws Exception;

    User validateUser(Long userId, CertificateType requestedType, CertificateType validateType);
    void validateRequest(CertificateAuthority certificateAuthority, Date validFrom, Date validTo) throws Exception;
    CertificateDetailsDTO getCertificate(CertificateAuthority cer) throws Exception;
}
