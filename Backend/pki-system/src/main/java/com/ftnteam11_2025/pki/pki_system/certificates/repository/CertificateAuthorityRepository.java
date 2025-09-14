package com.ftnteam11_2025.pki.pki_system.certificates.repository;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateAuthorityRepository extends JpaRepository<CertificateAuthority, Integer> {
}
