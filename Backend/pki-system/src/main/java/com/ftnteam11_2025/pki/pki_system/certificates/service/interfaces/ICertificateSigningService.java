package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateSigningRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.OrganizationCACertificatesResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.security.cert.Certificate;
import java.util.List;

public interface ICertificateSigningService {
    OrganizationCACertificatesResponseDTO getOrganizationCACertificates(Long userId);
    void createCSRAutogenerate(Long userId, CertificateSigningRequestDTO request) throws Exception;
    void createCSRSelfgenerate(Long userId, String caCertificateId, String validTo, MultipartFile pemFile);
}
