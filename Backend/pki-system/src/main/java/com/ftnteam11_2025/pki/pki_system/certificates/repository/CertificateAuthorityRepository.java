package com.ftnteam11_2025.pki.pki_system.certificates.repository;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificateAuthorityRepository extends JpaRepository<CertificateAuthority, UUID> {
    List<CertificateAuthority> findAllByStatusAndTypeNot(CertificateStatus status, CertificateType type);
    List<CertificateAuthority> findAllByOrganization(Organization organization);
}
