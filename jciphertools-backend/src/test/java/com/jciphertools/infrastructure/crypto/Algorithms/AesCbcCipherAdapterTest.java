package com.jciphertools.infrastructure.crypto.Algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.GeneralSecurityException;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

class AesCbcCipherAdapterTest {

    private static final String KEY_BASE64 = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes());
    private static final String IV_BASE64 = Base64.getEncoder()
            .encodeToString("0123456789abcdef".getBytes());

    private final AesCbcCipherAdapter adapter = new AesCbcCipherAdapter(KEY_BASE64, IV_BASE64);

    @Test
    void shouldEncryptAndDecryptRoundtrip() throws GeneralSecurityException {
        CipherRequest encryptRequest = new CipherRequest("hello world", Algorithm.AES_CBC_256);

        CipherResponse encrypted = adapter.encrypt(encryptRequest);
        CipherResponse decrypted = adapter.decrypt(new CipherRequest(encrypted.result(), Algorithm.AES_CBC_256));

        assertNotEquals("hello world", encrypted.result());
        assertEquals("hello world", decrypted.result());
    }

    @Test
    void shouldSupportMultipleInputLengths() throws GeneralSecurityException {
        assertRoundtrip("a");
        assertRoundtrip("0123456789abcdef");
        assertRoundtrip("0123456789abcdef0123456789abcdef");
        assertRoundtrip("x".repeat(128));
    }

    @Test
    void shouldThrowForInvalidBase64Ciphertext() {
        CipherRequest invalid = new CipherRequest("!!!invalid-base64!!!", Algorithm.AES_CBC_256);

        assertThrows(IllegalArgumentException.class, () -> adapter.decrypt(invalid));
    }

    private void assertRoundtrip(String plaintext) throws GeneralSecurityException {
        CipherResponse encrypted = adapter.encrypt(new CipherRequest(plaintext, Algorithm.AES_CBC_256));
        CipherResponse decrypted = adapter.decrypt(new CipherRequest(encrypted.result(), Algorithm.AES_CBC_256));
        assertEquals(plaintext, decrypted.result());
    }
}