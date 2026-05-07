package com.jciphertools.domain;

public record CipherRequest(String input, Algorithm algorithm) { 
        public CipherRequest {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input must not be blank");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm must not be null");
        }
    }
}
