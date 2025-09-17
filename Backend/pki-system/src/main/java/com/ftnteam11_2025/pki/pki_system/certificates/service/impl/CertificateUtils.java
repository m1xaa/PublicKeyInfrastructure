package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import java.security.*;
import java.util.Random;

public class CertificateUtils {
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        kpg.initialize(2048, random);
        return kpg.generateKeyPair();
    }
}
