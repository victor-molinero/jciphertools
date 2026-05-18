package com.jciphertools.infrastructure.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jciphertools.application.Exceptions.CipherOperationException;
import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.crypto.Algorithms.AesCbcCipherAdapter;
import com.jciphertools.infrastructure.crypto.Algorithms.AesGcmCipherAdapter;
import com.jciphertools.infrastructure.crypto.Algorithms.RsaOaepCipherAdapter;

@ExtendWith(MockitoExtension.class)
class CipherAdapterFactoryTest {

    @Mock
    private AesCbcCipherAdapter aesCbcAdapter;

    @Mock
    private AesGcmCipherAdapter aesGcmAdapter;

    @Mock
    private RsaOaepCipherAdapter rsaOaepAdapter;

    private CipherAdapterFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CipherAdapterFactory(aesCbcAdapter, aesGcmAdapter, rsaOaepAdapter);
    }

    @Test
    void shouldUseAesCbcAdapterForAesCbcEncryption() throws Exception {
        CipherRequest request = new CipherRequest("hello", Algorithm.AES_CBC_256);
        CipherResponse expected = new CipherResponse("encrypted");
        when(aesCbcAdapter.encrypt(request)).thenReturn(expected);

        CipherResponse actual = factory.encrypt(request);

        assertEquals("encrypted", actual.result());
        verify(aesCbcAdapter).encrypt(request);
    }

    @Test
    void shouldUseAesGcmAdapterForAesGcmEncryption() throws Exception {
        CipherRequest request = new CipherRequest("hello", Algorithm.AES_GCM_256);
        CipherResponse expected = new CipherResponse("encrypted");
        when(aesGcmAdapter.encrypt(request)).thenReturn(expected);

        CipherResponse actual = factory.encrypt(request);

        assertEquals("encrypted", actual.result());
        verify(aesGcmAdapter).encrypt(request);
    }

    @Test
    void shouldUseRsaOaepAdapterForRsaEncryption() throws Exception {
        CipherRequest request = new CipherRequest("hello", Algorithm.RSA_OAEP);
        CipherResponse expected = new CipherResponse("encrypted");
        when(rsaOaepAdapter.encrypt(request)).thenReturn(expected);

        CipherResponse actual = factory.encrypt(request);

        assertEquals("encrypted", actual.result());
        verify(rsaOaepAdapter).encrypt(request);
    }

    @Test
    void shouldUseAesCbcAdapterForAesCbcDecryption() throws Exception {
        CipherRequest request = new CipherRequest("ciphertext", Algorithm.AES_CBC_256);
        CipherResponse expected = new CipherResponse("decrypted");
        when(aesCbcAdapter.decrypt(request)).thenReturn(expected);

        CipherResponse actual = factory.decrypt(request);

        assertEquals("decrypted", actual.result());
        verify(aesCbcAdapter).decrypt(request);
    }

    @Test
    void shouldWrapEncryptionExceptionsAsCipherOperationException() throws Exception {
        CipherRequest request = new CipherRequest("hello", Algorithm.AES_CBC_256);
        GeneralSecurityException cause = new GeneralSecurityException("boom");
        when(aesCbcAdapter.encrypt(any(CipherRequest.class))).thenThrow(cause);

        CipherOperationException ex = assertThrows(CipherOperationException.class, () -> factory.encrypt(request));

        assertEquals("Cipher operation failed", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void shouldWrapDecryptionExceptionsAsCipherOperationException() throws Exception {
        CipherRequest request = new CipherRequest("ciphertext", Algorithm.AES_GCM_256);
        GeneralSecurityException cause = new GeneralSecurityException("boom");
        when(aesGcmAdapter.decrypt(any(CipherRequest.class))).thenThrow(cause);

        CipherOperationException ex = assertThrows(CipherOperationException.class, () -> factory.decrypt(request));

        assertEquals("Cipher operation failed", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}