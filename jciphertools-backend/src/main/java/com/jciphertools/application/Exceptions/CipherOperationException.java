package com.jciphertools.application.Exceptions;

public class CipherOperationException extends RuntimeException {
    public CipherOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}