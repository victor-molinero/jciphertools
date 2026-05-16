package com.jciphertools.application.UseCases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jciphertools.application.Interfaces.CipherPort;
import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

@Service
public class DecryptUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(DecryptUseCase.class);
    
    private final CipherPort cipherPort;

    public DecryptUseCase(CipherPort cipherPort) {
        this.cipherPort = cipherPort;
    }

    public CipherResponse execute(CipherRequest request){
        long startTime = System.currentTimeMillis();
        log.info("Decrypt use case execution initiated for algorithm: {}", request.algorithm());
        try {
            CipherResponse response = cipherPort.decrypt(request);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Decrypt use case completed successfully in {}ms", duration);
            return response;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Decrypt use case failed after {}ms: {}", duration, ex.getMessage());
            throw ex;
        }
    }
}
