package com.ftnteam11_2025.pki.pki_system.security.refresh.repository;

import com.ftnteam11_2025.pki.pki_system.security.refresh.model.RefreshToken;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
