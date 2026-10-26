package com.example.Transaction_Learning.DTO;

import java.time.Instant;

import org.springframework.http.HttpStatus;

/**
 * Uniform error body returned by the global {@code ErrorHandler}.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp) {

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                Instant.now());
    }
}
