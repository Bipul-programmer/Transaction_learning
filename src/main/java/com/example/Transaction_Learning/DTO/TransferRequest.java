package com.example.Transaction_Learning.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record TransferRequest(

        @NotNull
        @Min(1)
        Long fromId,
        @NotNull
        @Min(1)
        Long toId,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount
) {}