package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevokeCertificateDTO;

import java.util.UUID;

public interface ICertificateRevocationService {
    public void revokeCertificate(UUID certificateId, RevokeCertificateDTO request) throws Exception;
}
