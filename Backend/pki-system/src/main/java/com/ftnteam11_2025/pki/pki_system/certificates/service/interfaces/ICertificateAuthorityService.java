package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

public interface ICertificateAuthorityService {
    public CertificateAuthority createRootCA(CertificateRequestDTO requestDTO) throws Exception;
    public CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception;
    public CertificateResponseDTO createCertificateAuthority(CertificateRequestDTO requestDTO) throws Exception;
    public List<CertificateResponseDTO> getParentCertificate();
    public Resource downloadCertificateAuthority(UUID id) throws Exception;
}
