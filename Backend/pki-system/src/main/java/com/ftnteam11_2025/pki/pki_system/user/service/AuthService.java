package com.ftnteam11_2025.pki.pki_system.user.service;

import com.ftnteam11_2025.pki.pki_system.email.service.EmailService;
import com.ftnteam11_2025.pki.pki_system.security.jwt.JwtService;
import com.ftnteam11_2025.pki.pki_system.security.user.UserDetailsImpl;
import com.ftnteam11_2025.pki.pki_system.user.dto.*;
import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import com.ftnteam11_2025.pki.pki_system.user.model.AccountStatus;
import com.ftnteam11_2025.pki.pki_system.user.model.RegistrationRequest;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.AccountRepository;
import com.ftnteam11_2025.pki.pki_system.user.repository.RegistrationRequestRepository;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.util.VerificationCodeGenerator;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import com.ftnteam11_2025.pki.pki_system.util.exception.UnauthenticatedError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

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
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

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

    public LoginResponseDTO login(@Valid LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl  userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getUserId()).orElseThrow(()-> new UnauthenticatedError("Invalid credentials"));

            String jwt = jwtService.generateToken(userDetails);
            return new LoginResponseDTO(
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    userDetails.getUserRole(),
                    jwt,
                    null
            );
        }catch (DisabledException e) {
            throw new UnauthenticatedError("Account has been deactivated");
        } catch (BadCredentialsException e) {
            throw new UnauthenticatedError("Invalid credentials");
        } catch (AuthenticationException e) {
            throw new UnauthenticatedError(e.getMessage());
        }
    }
}
