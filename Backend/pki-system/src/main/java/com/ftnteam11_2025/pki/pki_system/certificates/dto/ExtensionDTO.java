package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExtensionDTO {
    private String name; // "BasicConstraints", "KeyUsage"
    private Map<String, Object> params;
}
