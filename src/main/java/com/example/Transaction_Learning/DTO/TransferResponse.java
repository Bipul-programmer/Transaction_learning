package com.example.Transaction_Learning.DTO;
import com.example.Transaction_Learning.Model.TransferRecord;
import com.example.Transaction_Learning.Model.TransferStatus;
import java.math.BigDecimal;
import java.time.Instant;
public record TransferResponse(
    Long transferId,
    Long fromAccountId,
    Long toAccountId,
    BigDecimal amount,
    TransferStatus status,
    Instant createdAt

) {
    public static TransferResponse from(TransferRecord r) {
        return new TransferResponse(
                r.getId(),
                r.getFromId(),
                r.getToId(),
                r.getAmount(),
                r.getStatus(),
                r.getCreatedAt());
        }
}
