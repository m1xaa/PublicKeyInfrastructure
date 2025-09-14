package com.ftnteam11_2025.pki.pki_system.config;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class BouncyCastleConfig {
    @PostConstruct
    public void addProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
