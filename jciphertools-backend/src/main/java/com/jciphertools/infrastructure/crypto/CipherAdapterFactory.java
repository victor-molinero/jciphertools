package com.jciphertools.infrastructure.crypto;

import java.security.GeneralSecurityException;

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

    private final AesCbcCipherAdapter _aesCbcAdapter;
    private final AesGcmCipherAdapter _aesGcmAdapter;
    private final RsaOaepCipherAdapter _rsaOaepAdapter;

    public CipherAdapterFactory(AesCbcCipherAdapter aesCbcAdapter,
            AesGcmCipherAdapter aesGcmAdapter,
            RsaOaepCipherAdapter rsaOaepAdapter) {
        this._aesCbcAdapter = aesCbcAdapter;
        this._aesGcmAdapter = aesGcmAdapter;
        this._rsaOaepAdapter = rsaOaepAdapter;
    }

    @Override
    public CipherResponse encrypt(CipherRequest request) {
        try {
            return resolve(request.algorithm()).encrypt(request);
        } catch (GeneralSecurityException ex) {
            throw new CipherOperationException("Cipher operation failed", ex);
        }
    }

    @Override
    public CipherResponse decrypt(CipherRequest request) {
        try {
            return resolve(request.algorithm()).decrypt(request);
        } catch (GeneralSecurityException ex) {
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
