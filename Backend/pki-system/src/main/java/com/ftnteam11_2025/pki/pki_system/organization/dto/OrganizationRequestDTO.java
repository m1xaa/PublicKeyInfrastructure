package com.ftnteam11_2025.pki.pki_system.organization.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationRequestDTO {
    private String name;
    private String alias;
    private String encryptedKeyStorePassword;
    private String encryptedPrivateKeyPassword;
    private String ksFilePath;
}
