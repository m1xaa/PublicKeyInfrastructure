package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseCard;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateOverviewService;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateOverviewService implements ICertificateOverviewService {

    private final UserService userService;
    private final CertificateAuthorityRepository certificateAuthorityRepository;

    @Override
    public List<CertificateResponseCard> getCertificatesByUserId(Long id) {
        User user = userService.findById(id);
        return switch (user.getRole()) {
            case REGULAR -> getCertificatesForEndEntity(user);
            case CA -> getCertificatesForCA(user);
            case ADMINISTRATOR -> getCertificatesForAdmin();
        };
    }


    private List<CertificateResponseCard> getCertificatesForEndEntity(User owner) {
        List<CertificateResponseCard> cards = certificateAuthorityRepository.findAllByOwner(owner).stream()
                .map(this::convertToCard).toList();
        this.removeIssuerForRootOwnersCerts(cards);
        return cards;
    }

    private List<CertificateResponseCard> getCertificatesForCA(User owner) {
        List<CertificateResponseCard> cards = certificateAuthorityRepository.findAllByOwner(owner).stream()
                .map(this::convertToCard).toList();
        this.removeIssuerForRootOwnersCerts(cards);
        List<CertificateResponseCard> allCerts = new ArrayList<>(cards);
        getCertificatesRecursively(cards, allCerts);
        return allCerts;
    }

    private List<CertificateResponseCard> getCertificatesForAdmin() {
        return certificateAuthorityRepository.findAll().stream()
                .map(this::convertToCard)
                .toList();
    }

    private void getCertificatesRecursively(List<CertificateResponseCard> ownersCertificates, List<CertificateResponseCard> allCertificates) {
        for (CertificateResponseCard cer: ownersCertificates) {
            List<CertificateResponseCard> newCerts = certificateAuthorityRepository.findAllByIssuerId(cer.getId());
            allCertificates.addAll(newCerts);
            getCertificatesRecursively(newCerts, allCertificates);
        }
    }

    private void removeIssuerForRootOwnersCerts(List<CertificateResponseCard> certs) {
        certs.forEach(cert -> cert.setIssuerId(null));
    }

    private CertificateResponseCard convertToCard(CertificateAuthority cer) {
        UUID issuerId = (cer.getIssuer() != null) ? cer.getIssuer().getId() : null;

        return CertificateResponseCard.builder()
                .id(cer.getId())
                .issuerId(issuerId)
                .email(cer.getOwner().getAccount().getEmail())
                .validFrom(cer.getValidFrom())
                .validTo(cer.getValidTo())
                .status(cer.getStatus())
                .commonName(cer.getCommon_name())
                .build();
    }
}
