package com.example.Transaction_Learning.Controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.Transaction_Learning.DTO.ErrorResponse;
import com.example.Transaction_Learning.Exceptions.InsufficientFundsException;
import com.example.Transaction_Learning.Exceptions.InvalidTransferException;
import com.example.Transaction_Learning.Idempotency.IdempotencyConflictException;

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    ResponseEntity<ErrorResponse> funds() {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient funds");
    }

    @ExceptionHandler(InvalidTransferException.class)
    ResponseEntity<ErrorResponse> invalid(InvalidTransferException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    ResponseEntity<ErrorResponse> conflict(IdempotencyConflictException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ResponseEntity<ErrorResponse> missingHeader(MissingRequestHeaderException e) {
        return build(HttpStatus.BAD_REQUEST, "Missing header: " + e.getHeaderName());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, message));
    }
}