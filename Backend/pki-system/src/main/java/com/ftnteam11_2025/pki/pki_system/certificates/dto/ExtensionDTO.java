package com.ftnteam11_2025.pki.pki_system.certificates.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExtensionDTO {
    private Integer pathLen;
    private boolean basicConstraints; // Must in Root, CA
    private boolean keyCertSign; // Must Root, CA
    private boolean crlSign; // Must Root, CA
    private boolean digitalSignature; // Must EE
    private boolean keyEncipherment; // Must EE
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