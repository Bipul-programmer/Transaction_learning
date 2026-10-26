package com.example.Transaction_Learning.Exceptions;

/**
 * Thrown when transfer request parameters are logically invalid
 * (e.g. same source and destination account, account not found).
 */
public class InvalidTransferException extends RuntimeException {
    public InvalidTransferException(String message) {
        super(message);
    }
}
