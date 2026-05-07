package com.jciphertools.infrastructure.crypto;

import java.security.GeneralSecurityException;

import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

public interface CipherAdapter {
    CipherResponse encrypt(CipherRequest request) throws GeneralSecurityException;
    CipherResponse decrypt(CipherRequest request) throws GeneralSecurityException;
}
