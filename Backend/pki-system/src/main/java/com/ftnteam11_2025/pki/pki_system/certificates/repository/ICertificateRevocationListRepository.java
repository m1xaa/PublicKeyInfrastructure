package com.ftnteam11_2025.pki.pki_system.certificates.repository;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateRevocationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICertificateRevocationListRepository extends JpaRepository<CertificateRevocationList, UUID> {
    Optional<CertificateRevocationList> findByCertificateAuthority(CertificateAuthority certificateAuthority);
}
