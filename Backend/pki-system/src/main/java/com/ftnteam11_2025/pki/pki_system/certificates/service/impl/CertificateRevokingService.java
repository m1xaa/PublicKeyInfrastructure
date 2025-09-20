package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.RevokeCertificateDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateRevokingService;

import java.util.UUID;

public class CertificateRevokingService implements ICertificateRevokingService {
    @Override
    public void revokeCertificate(UUID certificateId, RevokeCertificateDTO request) {

    }
}
