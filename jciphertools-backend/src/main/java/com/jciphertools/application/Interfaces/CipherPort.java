package com.jciphertools.application.Interfaces;

import com.jciphertools.domain.CipherRequest;
import com.jciphertools.domain.CipherResponse;

public interface CipherPort {
    CipherResponse encrypt(CipherRequest request);
    CipherResponse decrypt(CipherRequest request);
}
