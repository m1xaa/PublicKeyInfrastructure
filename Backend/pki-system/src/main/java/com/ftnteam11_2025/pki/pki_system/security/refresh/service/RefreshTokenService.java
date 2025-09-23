package com.ftnteam11_2025.pki.pki_system.security.refresh.service;

import com.ftnteam11_2025.pki.pki_system.security.exception.TokenRefreshException;
import com.ftnteam11_2025.pki.pki_system.security.jwt.JwtService;
import com.ftnteam11_2025.pki.pki_system.security.refresh.RefreshTokenConfigurationProperties;
import com.ftnteam11_2025.pki.pki_system.security.refresh.dto.RefreshTokenRequest;
import com.ftnteam11_2025.pki.pki_system.security.refresh.dto.RefreshTokenResponse;
import com.ftnteam11_2025.pki.pki_system.security.refresh.model.RefreshToken;
import com.ftnteam11_2025.pki.pki_system.security.refresh.repository.RefreshTokenRepository;
import com.ftnteam11_2025.pki.pki_system.security.user.UserDetailsImpl;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenConfigurationProperties refreshTokenConfigurationProperties;


    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setIssuedAt(new Date(System.currentTimeMillis()));
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis()+refreshTokenConfigurationProperties.getExpirationTimeMilliseconds()));
        String token = UUID.randomUUID().toString();
        refreshToken.setToken(this.hashToken(token));
        refreshToken = refreshTokenRepository.save(refreshToken);
        refreshToken.setToken(token);
        return refreshToken;
    }

    public RefreshTokenResponse refreshJwtToken(RefreshTokenRequest request){
        String requestRefreshToken = this.hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(), "Refresh token not found!"));
        UserDetailsImpl userDetails = new UserDetailsImpl(refreshToken.getUser().getAccount());

        if(!this.isRefreshTokenExpired(refreshToken)){
            String newAccessToken = jwtService.generateToken(userDetails);
            return new RefreshTokenResponse(newAccessToken, requestRefreshToken);
        }
        else {
            refreshTokenRepository.delete(refreshToken);
            throw  new TokenRefreshException(requestRefreshToken, "Refresh token expired!");
        }

    }

    private boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().toInstant().isBefore(Instant.now());
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
