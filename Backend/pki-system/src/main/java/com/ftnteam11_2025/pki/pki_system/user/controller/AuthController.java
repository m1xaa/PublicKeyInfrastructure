package com.ftnteam11_2025.pki.pki_system.user.controller;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.VerificationCodeDTO;
import com.ftnteam11_2025.pki.pki_system.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @ModelAttribute RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequestDTO));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@Valid @RequestBody VerificationCodeDTO dto) {
        authService.activateAccount(dto);
        return ResponseEntity.noContent().build();
    }
}
