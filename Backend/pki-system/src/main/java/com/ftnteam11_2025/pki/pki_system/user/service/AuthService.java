package com.ftnteam11_2025.pki.pki_system.user.service;

import com.ftnteam11_2025.pki.pki_system.email.service.EmailService;
import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.VerificationCodeDTO;
import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import com.ftnteam11_2025.pki.pki_system.user.model.AccountStatus;
import com.ftnteam11_2025.pki.pki_system.user.model.RegistrationRequest;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.AccountRepository;
import com.ftnteam11_2025.pki.pki_system.user.repository.RegistrationRequestRepository;
import com.ftnteam11_2025.pki.pki_system.util.VerificationCodeGenerator;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int VERIFICATION_CODE_LENGTH = 64;


    private final AccountRepository  accountRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Value("${activation.time.limit}")
    private Duration activationTimeLimit;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final EmailService emailService;

    public RegisterResponseDTO register(@Valid RegisterRequestDTO registerRequestDTO) {
        if(accountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new InvalidRequestError("Email address is already taken");
        }

        User user = userService.createUser(registerRequestDTO);

        RegistrationRequest registrationRequest =RegistrationRequest.builder()
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .user(user)
                .expirationTime(Instant.now().plus(activationTimeLimit))
                .verificationCode(VerificationCodeGenerator.generateVerificationCode(VERIFICATION_CODE_LENGTH))
                .build();


        emailService.sendAccountActivationEmail(registrationRequest);

        registrationRequestRepository.save(registrationRequest);

        return new RegisterResponseDTO(
                registrationRequest.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    @Transactional
    public void activateAccount(@Valid VerificationCodeDTO dto) {
        RegistrationRequest registrationRequest
                = registrationRequestRepository.findByVerificationCode(dto.getVerificationCode())
                .orElseThrow(() -> new NotFoundError("Activation code invalid or expired"));

        if (accountRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new InvalidRequestError("Email address is already taken");
        }

        if (registrationRequest.getExpirationTime().isBefore(Instant.now())) {
            registrationRequestRepository.delete(registrationRequest);
            throw new NotFoundError("Activation code invalid or expired");
        }

        Account account = Account.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .user(registrationRequest.getUser())
                .status(AccountStatus.ACTIVE)
                .build();

        registrationRequest.getUser().setAccount(account);

        accountRepository.save(account);
        registrationRequestRepository.delete(registrationRequest);
    }
}
