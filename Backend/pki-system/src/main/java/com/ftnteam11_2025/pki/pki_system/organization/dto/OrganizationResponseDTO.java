package com.ftnteam11_2025.pki.pki_system.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrganizationResponseDTO {
    private Long id;
    private String name;
    private String encryptedKeyStorePassword;
    private String encryptedPrivateKeyPassword;
    private Date createdAt;
}
