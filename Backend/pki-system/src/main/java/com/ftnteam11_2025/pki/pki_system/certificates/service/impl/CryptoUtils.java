package com.ftnteam11_2025.pki.pki_system.certificates.service.impl;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {



    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12; // init vector
    private static final int TAG_LENGTH_BIT = 128;


    // encrypt plaintext with master key -> get cipher text
    public static String encrypt(String plaintext, String masterKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecureRandom rng = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        rng.nextBytes(iv);

        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        SecretKey key = new SecretKeySpec(masterKeyBytes, AES);
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // store as Base64(iv || cipherText)
        byte[] out = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(cipherText, 0, out, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(out);
    }

    public static String decrypt(String base64IvAndCipherText, String masterKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ivAndCipher = Base64.getDecoder().decode(base64IvAndCipherText);
        if (ivAndCipher.length < IV_LENGTH) throw new IllegalArgumentException("Invalid data");

        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(ivAndCipher, 0, iv, 0, IV_LENGTH);
        int cipherLen = ivAndCipher.length - IV_LENGTH;
        byte[] cipherText = new byte[cipherLen];
        System.arraycopy(ivAndCipher, IV_LENGTH, cipherText, 0, cipherLen);

        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        SecretKey key = new SecretKeySpec(masterKeyBytes, AES);
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plain = cipher.doFinal(cipherText);
        return new String(plain, StandardCharsets.UTF_8);
    }
}
