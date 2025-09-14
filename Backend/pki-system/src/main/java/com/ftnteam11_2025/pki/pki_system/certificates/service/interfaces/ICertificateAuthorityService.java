package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;

public interface ICertificateAuthorityService {
    public CertificateAuthority createCA(CertificateRequestDTO requestDTO) throws Exception;
}
