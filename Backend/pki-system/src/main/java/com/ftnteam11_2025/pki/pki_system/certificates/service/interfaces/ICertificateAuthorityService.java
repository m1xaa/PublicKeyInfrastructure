package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

public interface ICertificateAuthorityService {
    public CertificateAuthority createRootCA(CertificateRequestDTO requestDTO) throws Exception;
    public CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception;
    public CertificateAuthority createCertificateAuthority(CertificateRequestDTO requestDTO) throws Exception;
    public Organization saveTransfer(String name, PrivateKey pk, X509Certificate certificate, CertificateType type) throws Exception;
    public List<CertificateResponseDTO> getParentCertificate();
}
