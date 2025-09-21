package com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseCard;

import java.util.List;

public interface ICertificateOverviewService {
    List<CertificateResponseCard> getCertificatesByUserId(Long id);
}
