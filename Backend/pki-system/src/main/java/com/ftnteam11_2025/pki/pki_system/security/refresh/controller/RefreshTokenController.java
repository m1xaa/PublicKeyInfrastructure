package com.ftnteam11_2025.pki.pki_system.security.refresh.controller;

import com.ftnteam11_2025.pki.pki_system.security.refresh.dto.RefreshTokenRequest;
import com.ftnteam11_2025.pki.pki_system.security.refresh.dto.RefreshTokenResponse;
import com.ftnteam11_2025.pki.pki_system.security.refresh.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/refresh")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("")
    public ResponseEntity<RefreshTokenResponse> refreshJwtToken(@RequestBody RefreshTokenRequest request) {
       return ResponseEntity.status(HttpStatus.CREATED).body(refreshTokenService.refreshJwtToken(request));
    }

}
