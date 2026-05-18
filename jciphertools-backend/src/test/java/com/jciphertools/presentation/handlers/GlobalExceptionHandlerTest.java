package com.jciphertools.presentation.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.jciphertools.application.Exceptions.CipherOperationException;
import com.jciphertools.application.Exceptions.UnsupportedAlgorithmException;
import com.jciphertools.domain.Algorithm;
import com.jciphertools.presentation.dto.CipherRequestDto;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn400ForValidationErrors() throws Exception {
        Method method = DummyController.class.getDeclaredMethod("dummy", CipherRequestDto.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "cipherRequestDto");
        bindingResult.addError(new FieldError("cipherRequestDto", "input", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ProblemDetail> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getDetail());
        Object errors = response.getBody().getProperties().get("errors");
        assertInstanceOf(List.class, errors);
        assertTrue(((List<?>) errors).contains("input: must not be blank"));
    }

    @Test
    void shouldReturn400ForUnreadableBody() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "bad json",
            mock(HttpInputMessage.class));

        ResponseEntity<ProblemDetail> response = handler.handleUnreadable(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Malformed or unreadable request body", response.getBody().getDetail());
    }

    @Test
    void shouldReturn400ForIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Input must not be blank");

        ResponseEntity<ProblemDetail> response = handler.handleBadRequest(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Input must not be blank", response.getBody().getDetail());
    }

    @Test
    void shouldReturn400ForUnsupportedAlgorithm() {
        UnsupportedAlgorithmException ex = new UnsupportedAlgorithmException(Algorithm.AES_CBC_256);

        ResponseEntity<ProblemDetail> response = handler.handleUnsupported(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unsupported algorithm: AES_CBC_256", response.getBody().getDetail());
    }

    @Test
    void shouldReturn422ForCipherOperationErrors() {
        CipherOperationException ex = new CipherOperationException("failure", new GeneralSecurityException("cause"));

        ResponseEntity<ProblemDetail> response = handler.handleCryptoError(ex);

        assertEquals(422, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Cipher operation failed", response.getBody().getDetail());
    }

    @Test
    void shouldReturn500ForUnexpectedErrors() {
        RuntimeException ex = new RuntimeException("unexpected");

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getDetail());
    }

    static class DummyController {
        @SuppressWarnings("unused")
        void dummy(CipherRequestDto dto) {
        }
    }
}