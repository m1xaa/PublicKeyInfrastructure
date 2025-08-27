package com.ftnteam11_2025.pki.pki_system.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "java.email.config")
public class JavaMailSenderConfigProperties {
    @Email
    @NotBlank
    private String fromEmail;

    @NotBlank
    private String fromName;
}
