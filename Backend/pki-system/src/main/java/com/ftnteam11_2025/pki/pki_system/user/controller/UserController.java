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

    @Secured("ROLE_ADMIN")
    @PostMapping("/register/ca")
    public ResponseEntity<Boolean> registerCA(@Valid @ModelAttribute CARegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerCA(registerRequestDTO));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateAccount(@Valid @RequestBody VerificationCodeDTO dto) {
        authService.activateAccount(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activate/ca")
    public ResponseEntity<Void> activateCaAccount(@Valid @RequestBody CASetPasswordRequest dto) {
        authService.activateAccountCA(dto);
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

    @Secured({"ROLE_ADMIN", "ROLE_CA"})
    @GetMapping("/{name}")
    public ResponseEntity<List<UserResponseDTO>> getAllByOrganization(@PathVariable String name){
        return ResponseEntity.ok(userService.getAllUsersByOrganization(name));
    }
}
