package com.jciphertools.application.UseCases;

import org.springframework.stereotype.Service;

import com.jciphertools.application.Interfaces.CipherPort;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

@Service
public class DecryptUseCase {
    
    private final CipherPort cipherPort;

    public DecryptUseCase(CipherPort cipherPort) {
        this.cipherPort = cipherPort;
    }

    public CipherResponse execute(CipherRequest request){
        return cipherPort.decrypt(request);
    }
}
