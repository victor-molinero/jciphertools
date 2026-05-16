package com.jciphertools.application.UseCases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jciphertools.application.Interfaces.CipherPort;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

@Service
public class EncryptUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(EncryptUseCase.class);
    
    private final CipherPort cipherPort;

    public EncryptUseCase(CipherPort cipherPort) {
        this.cipherPort = cipherPort;
    }

    public CipherResponse execute(CipherRequest request){
        long startTime = System.currentTimeMillis();
        log.info("Encrypt use case execution initiated for algorithm: {}", request.algorithm());
        try {
            CipherResponse response = cipherPort.encrypt(request);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Encrypt use case completed successfully in {}ms", duration);
            return response;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Encrypt use case failed after {}ms: {}", duration, ex.getMessage());
            throw ex;
        }
    }
}
