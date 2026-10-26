package com.example.Transaction_Learning.Command;

import java.math.BigDecimal;

/**
 * Immutable value object representing a transfer request coming from the HTTP layer.
 */
public record TransferCommand(
        String idempotencyKey,
        Long fromId,
        Long toId,
        BigDecimal amount) {
}
