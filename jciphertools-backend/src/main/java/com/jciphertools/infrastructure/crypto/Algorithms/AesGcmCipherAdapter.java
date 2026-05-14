package com.jciphertools.infrastructure.crypto.Algorithms;

import java.util.Base64;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javax.crypto.Cipher;

import java.security.GeneralSecurityException;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.crypto.CipherAdapter;

@Component
public class AesGcmCipherAdapter implements CipherAdapter {
    
    private static final String AES_ALGO = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;
    private final IvParameterSpec iv;

    public AesGcmCipherAdapter(
            @Value("${cipher.aes.gcm.key}") String base64Key,
            @Value("${cipher.aes.gcm.iv}") String base64Iv) {

        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        byte[] ivBytes  = Base64.getDecoder().decode(base64Iv);
        this.secretKey = new SecretKeySpec(keyBytes, AES_ALGO);
        this.iv        = new IvParameterSpec(ivBytes);           
    }

    @Override
    public CipherResponse encrypt(CipherRequest request) throws GeneralSecurityException  {

        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv.getIV());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] ciphertext = cipher.doFinal(request.input()
        .getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return new CipherResponse(Base64.getEncoder().encodeToString(ciphertext));
    }

    @Override
    public CipherResponse decrypt(CipherRequest request)  throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv.getIV());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decodedCiphertext = Base64.getDecoder().decode(request.input());
        byte[] plaintext = cipher.doFinal(decodedCiphertext);
        return new CipherResponse(new String(plaintext, java.nio.charset.StandardCharsets.UTF_8));
    }
}