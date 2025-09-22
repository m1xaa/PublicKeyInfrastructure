package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseCard;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateOverviewService;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
        List<CertificateAuthority> roots = certificateAuthorityRepository.findAllByIssuerIsNull();
        List<CertificateResponseCard> result = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();

        getCertificatesRecursively(roots, result, owner.getId(), false, visited);
        return result;
    }

    private List<CertificateResponseCard> getCertificatesForAdmin() {
        return certificateAuthorityRepository.findAll().stream()
                .map(this::convertToCard)
                .toList();
    }

    private void getCertificatesRecursively(
            List<CertificateAuthority> current,
            List<CertificateResponseCard> result,
            Long ownerId,
            boolean shouldAdd,
            Set<UUID> visited
    ) {
        for (CertificateAuthority cer : current) {
            if (!visited.add(cer.getId())) continue;

            boolean sameOwner = cer.getOwner().getId().equals(ownerId);
            boolean nextShouldAdd = shouldAdd || sameOwner;

            if (sameOwner || shouldAdd) {
                CertificateResponseCard dto = convertToCard(cer);
                if (sameOwner && !shouldAdd) {
                    dto.setIssuerId(null); // only top owned cert loses issuer for front tree like overview
                }
                result.add(dto);
            }

            List<CertificateAuthority> children = certificateAuthorityRepository.findAllByIssuer(cer);
            getCertificatesRecursively(children, result, ownerId, nextShouldAdd, visited);
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
