package com.jciphertools.presentation.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.jciphertools.domain.Algorithm;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CipherRequestDtoTest {

    private final Validator validator;

    CipherRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void shouldHaveNoViolationsForValidDto() {
        CipherRequestDto dto = new CipherRequestDto("hello", Algorithm.AES_CBC_256);

        Set<ConstraintViolation<CipherRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenInputIsBlank() {
        CipherRequestDto dto = new CipherRequestDto("", Algorithm.AES_CBC_256);

        Set<ConstraintViolation<CipherRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<CipherRequestDto> violation = violations.iterator().next();
        assertEquals("input", violation.getPropertyPath().toString());
    }

    @Test
    void shouldFailWhenAlgorithmIsNull() {
        CipherRequestDto dto = new CipherRequestDto("hello", null);

        Set<ConstraintViolation<CipherRequestDto>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
        ConstraintViolation<CipherRequestDto> violation = violations.iterator().next();
        assertEquals("algorithm", violation.getPropertyPath().toString());
    }
}