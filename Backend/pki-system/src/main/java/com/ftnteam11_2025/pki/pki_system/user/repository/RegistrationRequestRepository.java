package com.ftnteam11_2025.pki.pki_system.user.repository;

import com.ftnteam11_2025.pki.pki_system.user.model.RegistrationRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    Optional<RegistrationRequest> findByVerificationCode(@NotNull String verificationCode);

}
