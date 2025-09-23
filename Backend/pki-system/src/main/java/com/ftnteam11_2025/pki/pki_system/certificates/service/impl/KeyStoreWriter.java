package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import org.springframework.stereotype.Component;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Component
public class KeyStoreWriter {
    private KeyStore keyStore;

    public KeyStoreWriter() {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void loadKeyStore(String fileName, char[] password) {
        try {
            File ksFile = new File(fileName);

            File parentDir = ksFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (ksFile.exists()) {
                try (FileInputStream fis = new FileInputStream(ksFile)) {
                    keyStore.load(fis, password);
                }
            } else {
                keyStore.load(null, password);
                try (FileOutputStream fos = new FileOutputStream(ksFile)) {
                    keyStore.store(fos, password);
                } catch (KeyStoreException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
        try {
            if (privateKey != null) {
                keyStore.setKeyEntry(alias, privateKey, password, new Certificate[]{certificate});
            } else {
                keyStore.setCertificateEntry(alias, certificate);
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
