package com.ftnteam11_2025.pki.pki_system.user.controller;

import com.ftnteam11_2025.pki.pki_system.user.dto.*;
import com.ftnteam11_2025.pki.pki_system.user.service.AuthService;
import com.ftnteam11_2025.pki.pki_system.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @ModelAttribute RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequestDTO));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@Valid @RequestBody VerificationCodeDTO dto) {
        authService.activateAccount(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
