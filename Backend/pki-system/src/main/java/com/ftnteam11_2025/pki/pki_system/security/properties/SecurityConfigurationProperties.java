package com.ftnteam11_2025.pki.pki_system.security.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityConfigurationProperties {
    @NotNull
    private List<EndpointConfig> publicEndpoints;
    private String masterKey;
}
