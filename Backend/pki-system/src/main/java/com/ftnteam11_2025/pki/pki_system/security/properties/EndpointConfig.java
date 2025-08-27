package com.ftnteam11_2025.pki.pki_system.security.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EndpointConfig {
    private String path;
    private List<String> methods;
}
