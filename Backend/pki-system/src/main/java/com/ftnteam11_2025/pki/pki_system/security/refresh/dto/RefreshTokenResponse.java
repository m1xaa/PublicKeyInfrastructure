package com.ftnteam11_2025.pki.pki_system.security.refresh.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RefreshTokenResponse {
    private final String newJwtToken;
    private final String refreshToken;
}
