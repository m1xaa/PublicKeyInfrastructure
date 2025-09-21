package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExtensionDTO {
    private Integer pathLen; // root, ca
    private boolean subjectKeyIdentifier; // root
    private boolean authorityKeyIdentifier; // root
    private boolean serverAuth;
    private boolean clientAuth;

}

// Root, CA
/*
* Must have baicCOnstraints, keyCertSign, crlSign
* optional digitalSignature
* forbbiden keyEncipherment
*
*
*
* */

/* EE
* Must have digitalSignature, keyEncipherment
* firbidden basicConstraints, keyCertSign crlSign
*
* */