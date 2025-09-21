package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRevocationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevokeCertificateDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public interface ICertificateRevocationService {
    void revokeCertificate(UUID certificateId, RevokeCertificateDTO request) throws Exception;
    List<CertificateRevocationResponseDTO> getAll();
    Resource download(UUID id);
}
