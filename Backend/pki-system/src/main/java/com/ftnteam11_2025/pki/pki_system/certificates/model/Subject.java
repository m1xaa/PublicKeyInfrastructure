package com.ftnteam11_2025.pki.pki_system.certificates.model;

import lombok.*;
import org.bouncycastle.asn1.x500.X500Name;

import java.security.PublicKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subject {
    private PublicKey publicKey;
    private X500Name x500Name;
}
