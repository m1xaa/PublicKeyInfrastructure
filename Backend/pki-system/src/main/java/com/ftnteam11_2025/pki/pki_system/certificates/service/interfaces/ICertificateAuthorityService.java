package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateDetailsDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseCard;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

public interface ICertificateAuthorityService {
    CertificateAuthority createRootCA(CertificateRequestDTO requestDTO) throws Exception;
    CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception;
    CertificateResponseDTO createCertificateAuthority(CertificateRequestDTO requestDTO) throws Exception;
    List<CertificateResponseDTO> getParentCertificate();
    List<CertificateResponseDTO> getParentCertificateByOrganization(String name);
    Resource downloadCertificateAuthority(UUID id) throws Exception;
    List<CertificateResponseCard> getCertificates();
    CertificateDetailsDTO getCertificateDetails(UUID id) throws Exception;
    CertificateAuthority createEndEntityFromCSR(CertificateRequestDTO requestDTO, PublicKey publicKey) throws Exception;
}
