package com.example.Transaction_Learning.Model;

import java.util.EnumSet;
import java.util.Set;


public enum TransferStatus {
    PENDING,
    COMPLETED,
    FAILED;


    private Set<TransferStatus> allowedNext() {
        return switch(this) {
            case PENDING -> EnumSet.of(COMPLETED , FAILED);
            case COMPLETED -> EnumSet.noneOf(TransferStatus.class);  
            case FAILED    -> EnumSet.noneOf(TransferStatus.class); 
        };
    }


    public boolean canMoveTo(TransferStatus next) {
        return allowedNext().contains(next);
    }

    public boolean isTerminal() {
        return allowedNext().isEmpty();
    }
}