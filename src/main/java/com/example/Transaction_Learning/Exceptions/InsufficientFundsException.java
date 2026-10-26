package com.example.Transaction_Learning.Exceptions;

/**
 * Thrown when the source account does not have enough balance to cover the transfer amount.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
