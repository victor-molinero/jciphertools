package com.jciphertools.presentation.handlers;

import com.jciphertools.application.Exceptions.CipherOperationException;
import com.jciphertools.application.Exceptions.UnsupportedAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Request validation failed: {}", ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + "=" + e.getDefaultMessage())
            .toList());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Validation failed");
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
        }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed or unreadable request body received: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Malformed or unreadable request body"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Invalid argument received: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedAlgorithmException.class)
    public ResponseEntity<ProblemDetail> handleUnsupported(UnsupportedAlgorithmException ex) {
        log.warn("Unsupported algorithm requested: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage()));
    }

    @ExceptionHandler(CipherOperationException.class)
    public ResponseEntity<ProblemDetail> handleCryptoError(CipherOperationException ex) {
        log.error("Cipher operation failed with security exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(422)
                .body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), "Cipher operation failed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "An unexpected error occurred"));
    }
}
