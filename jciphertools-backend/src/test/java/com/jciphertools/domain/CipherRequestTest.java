package com.jciphertools.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CipherRequestTest {

    @Test
    void shouldCreateRequestWhenInputAndAlgorithmAreValid() {
        CipherRequest request = new CipherRequest("hello", Algorithm.AES_CBC_256);

        assertEquals("hello", request.input());
        assertEquals(Algorithm.AES_CBC_256, request.algorithm());
    }

    @Test
    void shouldThrowWhenInputIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CipherRequest(null, Algorithm.AES_CBC_256));

        assertEquals("Input must not be blank", ex.getMessage());
    }

    @Test
    void shouldThrowWhenInputIsBlank() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CipherRequest("", Algorithm.AES_CBC_256));

        assertEquals("Input must not be blank", ex.getMessage());
    }

    @Test
    void shouldThrowWhenInputIsWhitespace() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CipherRequest("   ", Algorithm.AES_CBC_256));

        assertEquals("Input must not be blank", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAlgorithmIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new CipherRequest("hello", null));

        assertEquals("Algorithm must not be null", ex.getMessage());
    }
}