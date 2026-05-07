package com.jciphertools.application;

import org.springframework.stereotype.Service;

import com.jciphertools.domain.CipherPort;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

@Service
public class EncryptUseCase {
    
    private final CipherPort cipherPort;

    public EncryptUseCase(CipherPort cipherPort) {
        this.cipherPort = cipherPort;
    }

    public CipherResponse execute(CipherRequest request){
        return cipherPort.encrypt(request);
    }    
}
