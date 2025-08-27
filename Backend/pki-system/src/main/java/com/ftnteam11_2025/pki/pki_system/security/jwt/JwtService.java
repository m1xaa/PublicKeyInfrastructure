package com.ftnteam11_2025.pki.pki_system.security.jwt;

import com.ftnteam11_2025.pki.pki_system.security.properties.JwtConfigurationProperties;
import com.ftnteam11_2025.pki.pki_system.security.user.UserDetailsImpl;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.security.Keys;


@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String AUTH_HEADER = "Authorization";
    private static final String HEADER_PREFIX = "Bearer ";

    private final JwtConfigurationProperties  jwtConfigurationProperties;

    private Key getSigningKey(){
        byte[] keyBytes = jwtConfigurationProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetailsImpl userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("user_id",userDetails.getUserId())
                .claim("account_id", userDetails.getAccountId())
                .claim("role", userDetails.getUserRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfigurationProperties.getExpirationTimeMilliseconds()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token){
        try {
            getAllClaimsFromToken(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public String getToken(HttpServletRequest request){
        final String header = request.getHeader(AUTH_HEADER);
        if(header != null && !header.startsWith(HEADER_PREFIX)){
            return header.substring(HEADER_PREFIX.length());
        }
        return null;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return new UserDetailsImpl(
                claims.get("user_id", Long.class),
                claims.get("account_id", Long.class),
                claims.getSubject(),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }
}
