package com.jciphertools.infrastructure.crypto.Algorithms;

import org.springframework.stereotype.Component;

import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.crypto.CipherAdapter;

@Component
public class AesCbcCipherAdapter implements CipherAdapter {
    @Override
    public CipherResponse encrypt(CipherRequest request) {
        // Implement AES-CBC encryption logic here
        return new CipherResponse("Encrypted data using AES-CBC");
    }

    @Override
    public CipherResponse decrypt(CipherRequest request) {
        // Implement AES-CBC decryption logic here
        return new CipherResponse("Decrypted data using AES-CBC");
    }
}