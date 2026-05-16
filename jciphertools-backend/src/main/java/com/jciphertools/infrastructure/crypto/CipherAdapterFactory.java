package com.jciphertools.infrastructure.crypto;

import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jciphertools.application.Exceptions.CipherOperationException;
import com.jciphertools.application.Interfaces.CipherPort;
import com.jciphertools.domain.Algorithm;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;
import com.jciphertools.infrastructure.crypto.Algorithms.AesCbcCipherAdapter;
import com.jciphertools.infrastructure.crypto.Algorithms.AesGcmCipherAdapter;
import com.jciphertools.infrastructure.crypto.Algorithms.RsaOaepCipherAdapter;

@Component
public class CipherAdapterFactory implements CipherPort {
    private static final Logger log = LoggerFactory.getLogger(CipherAdapterFactory.class);


    private final AesCbcCipherAdapter _aesCbcAdapter;
    private final AesGcmCipherAdapter _aesGcmAdapter;
    private final RsaOaepCipherAdapter _rsaOaepAdapter;

    public CipherAdapterFactory(AesCbcCipherAdapter aesCbcAdapter,
            AesGcmCipherAdapter aesGcmAdapter,
            RsaOaepCipherAdapter rsaOaepAdapter) {
        this._aesCbcAdapter = aesCbcAdapter;
        this._aesGcmAdapter = aesGcmAdapter;
        this._rsaOaepAdapter = rsaOaepAdapter;
        log.info("CipherAdapterFactory initialized with adapters: [AES-CBC-256, AES-GCM-256, RSA-OAEP]");
    }

    @Override
    public CipherResponse encrypt(CipherRequest request) {
        log.info("Encrypt operation initiated with algorithm: {}", request.algorithm());
        try {
            CipherAdapter adapter = resolve(request.algorithm());
            log.debug("Resolved adapter: {} for encryption", adapter.getClass().getSimpleName());
            return adapter.encrypt(request);
        } catch (GeneralSecurityException ex) {
            log.error("Encryption failed for algorithm: {} - {}", request.algorithm(), ex.getMessage());
            throw new CipherOperationException("Cipher operation failed", ex);
        }
    }

    @Override
    public CipherResponse decrypt(CipherRequest request) {
        log.info("Decrypt operation initiated with algorithm: {}", request.algorithm());
        try {
            CipherAdapter adapter = resolve(request.algorithm());
            log.debug("Resolved adapter: {} for decryption", adapter.getClass().getSimpleName());
            return adapter.decrypt(request);
        } catch (GeneralSecurityException ex) {
            log.error("Decryption failed for algorithm: {} - {}", request.algorithm(), ex.getMessage());
            throw new CipherOperationException("Cipher operation failed", ex);
        }
    }

    private CipherAdapter resolve(Algorithm algorithm) {
        return switch (algorithm) {
            case AES_CBC_256 -> _aesCbcAdapter;
            case AES_GCM_256 -> _aesGcmAdapter;
            case RSA_OAEP -> _rsaOaepAdapter;
        };
    }
}
