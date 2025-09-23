package com.ftnteam11_2025.pki.pki_system.security.refresh;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "security.refresh.token")
public class RefreshTokenConfigurationProperties {
    @NotNull
    @Positive
    private Long expirationTimeMilliseconds;
}
