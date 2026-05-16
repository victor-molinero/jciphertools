package com.jciphertools.infrastructure.crypto.Algorithms;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.crypto.CipherAdapter;


@Component
public class AesCbcCipherAdapter implements CipherAdapter {
    
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec secretKey;
    private final IvParameterSpec iv;

    private static final Logger log = LoggerFactory.getLogger(AesCbcCipherAdapter.class);



    public AesCbcCipherAdapter(
        @Value("${cipher.aes.cbc.key}") 
        String base64Key,
        @Value("${cipher.aes.cbc.iv}") 
        String base64Iv
    ) {
        
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        byte[] ivBytes  = Base64.getDecoder().decode(base64Iv);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
        this.iv        = new IvParameterSpec(ivBytes);
        log.debug("AES-CBC adapter initialized with key length {} bytes, IV length {} bytes", 
            keyBytes.length, ivBytes.length);
    }


    @Override
    public CipherResponse encrypt(CipherRequest request) throws GeneralSecurityException {
        log.debug("AES-CBC encryption starting");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);                
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(request.input()
        .getBytes(java.nio.charset.StandardCharsets.UTF_8));
        log.debug("AES-CBC encryption successful, output size: {} bytes", encrypted.length);
        return new CipherResponse(Base64.getEncoder().encodeToString(encrypted));
    }

    @Override
    public CipherResponse decrypt(CipherRequest request) throws GeneralSecurityException {
        log.debug("AES-CBC decryption starting");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(request.input()));
        log.debug("AES-CBC decryption successful");
        return new CipherResponse(new String(decrypted, java.nio.charset.StandardCharsets.UTF_8));
    }
}