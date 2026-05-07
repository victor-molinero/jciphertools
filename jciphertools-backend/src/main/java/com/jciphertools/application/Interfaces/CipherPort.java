package com.jciphertools.domain;

public interface CipherPort {
    CipherResponse encrypt(CipherRequest request);
    CipherResponse decrypt(CipherRequest request);
}
