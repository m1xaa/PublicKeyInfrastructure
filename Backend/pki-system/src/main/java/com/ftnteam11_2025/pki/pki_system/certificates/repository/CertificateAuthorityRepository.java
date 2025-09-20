package com.ftnteam11_2025.pki.pki_system.certificates.repository;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseCard;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateStatus;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateType;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CertificateAuthorityRepository extends JpaRepository<CertificateAuthority, UUID> {
    List<CertificateAuthority> findAllByStatusAndTypeNot(CertificateStatus status, CertificateType type);
    List<CertificateAuthority> findAllByOrganization(Organization organization);
    List<CertificateAuthority> findAllByStatusAndTypeNotAndOrganization(CertificateStatus status, CertificateType type, Organization organization);
    List<CertificateAuthority> findAllByOwner(User owner);
    List<CertificateAuthority> findAllByIssuer(CertificateAuthority issuer);
    @Query("""
    SELECT new com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseCard(
    c.id,
    c.issuer.id,
    c.common_name,
    c.owner.account.email,
    c.validFrom,
    c.validTo,
    c.status
    )
    FROM CertificateAuthority c
    WHERE c.issuer.id = :issuerId
""")
    List<CertificateResponseCard> findAllByIssuerId(@Param("issuerId") UUID issuerId);
}
