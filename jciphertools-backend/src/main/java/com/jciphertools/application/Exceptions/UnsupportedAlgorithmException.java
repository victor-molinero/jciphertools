package com.jciphertools.application.Exceptions;

import com.jciphertools.domain.Algorithm;

public class UnsupportedAlgorithmException extends RuntimeException {
    public UnsupportedAlgorithmException(Algorithm algorithm) {
        super("Unsupported algorithm: " + algorithm);
    }
}
