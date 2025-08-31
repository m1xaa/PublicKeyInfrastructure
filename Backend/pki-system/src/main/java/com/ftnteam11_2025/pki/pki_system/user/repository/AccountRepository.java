package com.ftnteam11_2025.pki.pki_system.user.repository;

import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}
