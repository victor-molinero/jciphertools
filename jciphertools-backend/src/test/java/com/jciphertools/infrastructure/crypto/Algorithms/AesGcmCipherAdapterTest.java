package com.jciphertools.infrastructure.crypto.Algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.AEADBadTagException;

import org.junit.jupiter.api.Test;

import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

class AesGcmCipherAdapterTest {

    private static final String KEY_BASE64 = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes());
    private static final String IV_BASE64 = Base64.getEncoder()
            .encodeToString("0123456789ab".getBytes());

    private final AesGcmCipherAdapter adapter = new AesGcmCipherAdapter(KEY_BASE64, IV_BASE64);

    @Test
    void shouldEncryptAndDecryptRoundtrip() throws GeneralSecurityException {
        CipherRequest encryptRequest = new CipherRequest("hello world", Algorithm.AES_GCM_256);

        CipherResponse encrypted = adapter.encrypt(encryptRequest);
        CipherResponse decrypted = adapter.decrypt(new CipherRequest(encrypted.result(), Algorithm.AES_GCM_256));

        assertNotEquals("hello world", encrypted.result());
        assertEquals("hello world", decrypted.result());
    }

    @Test
    void shouldFailDecryptionWhenCiphertextIsTampered() throws GeneralSecurityException {
        CipherResponse encrypted = adapter.encrypt(new CipherRequest("hello world", Algorithm.AES_GCM_256));
        byte[] tampered = Base64.getDecoder().decode(encrypted.result());
        tampered[0] = (byte) (tampered[0] ^ 0x01);
        String tamperedBase64 = Base64.getEncoder().encodeToString(tampered);

        assertThrows(AEADBadTagException.class,
                () -> adapter.decrypt(new CipherRequest(tamperedBase64, Algorithm.AES_GCM_256)));
    }

    @Test
    void shouldThrowForInvalidBase64Ciphertext() {
        CipherRequest invalid = new CipherRequest("###", Algorithm.AES_GCM_256);

        assertThrows(IllegalArgumentException.class, () -> adapter.decrypt(invalid));
    }
}