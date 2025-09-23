package com.ftnteam11_2025.pki.pki_system.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JavaMailSenderConfigProperties.class)
public class JavaMailSenderConfig {}
