package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.SubjectIssuerDTO;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Random;

public class CertificateUtils {
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        kpg.initialize(2048, random);
        return kpg.generateKeyPair();
    }
    public static SubjectIssuerDTO getIssuerOrSubject(X509Certificate cert, boolean issuer) {
        try {
            String dn = issuer ? cert.getIssuerX500Principal().getName()
                    : cert.getSubjectX500Principal().getName();

            LdapName ldapDN = new LdapName(dn);
            String cn = null, o = null, ou = null;

            for (Rdn rdn : ldapDN.getRdns()) {
                switch (rdn.getType()) {
                    case "CN":
                        cn = rdn.getValue().toString();
                        break;
                    case "O":
                        o = rdn.getValue().toString();
                        break;
                    case "OU":
                        ou = rdn.getValue().toString();
                        break;
                }
            }

            return new SubjectIssuerDTO(cn, o, ou);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getSHA256Fingerprint(X509Certificate cert) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] der = cert.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
