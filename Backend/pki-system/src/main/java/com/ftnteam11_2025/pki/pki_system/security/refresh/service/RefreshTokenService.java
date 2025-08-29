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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenConfigurationProperties refreshTokenConfigurationProperties;


    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);
        if(refreshToken != null){
            refreshToken.setIssuedAt(new Date(System.currentTimeMillis()));
            refreshToken.setExpiryDate(new Date(System.currentTimeMillis()+refreshTokenConfigurationProperties.getExpirationTimeMilliseconds()));
        }else{
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setIssuedAt(new Date(System.currentTimeMillis()));
            refreshToken.setExpiryDate(new Date(System.currentTimeMillis()+refreshTokenConfigurationProperties.getExpirationTimeMilliseconds()));
            refreshToken.setToken(UUID.randomUUID().toString());
        }
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenResponse refreshJwtToken(RefreshTokenRequest request){
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found!"));
        UserDetailsImpl userDetails = new UserDetailsImpl(refreshToken.getUser().getAccount());

        if(!this.isRefreshTokenExpired(refreshToken)){
            String newAccessToken = jwtService.generateToken(userDetails);
            return new RefreshTokenResponse(newAccessToken, requestRefreshToken);
        }
        else {
            throw  new TokenRefreshException(requestRefreshToken, "Refresh token expired!");
        }

    }

    private boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().toInstant().isBefore(Instant.now());
    }

}
