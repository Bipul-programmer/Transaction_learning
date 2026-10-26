package com.example.Transaction_Learning.Model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "transfer_record",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_transfer_idempotency_key",
            columnNames = "idempotency_key"
        )
    }
)
public class TransferRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    private String requestHash;

    private Long fromId;

    private Long toId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TransferRecord() {
        // Required by JPA
    }

    public TransferRecord(
            String key,
            String hash,
            Long fromId,
            Long toId,
            BigDecimal amount) {

        this.idempotencyKey = key;
        this.requestHash = hash;
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.status = TransferStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void complete() {
        this.status = TransferStatus.COMPLETED;
    }
    public void fail() { 
        transitionTo(TransferStatus.FAILED); 
    }

    private void transitionTo(TransferStatus next) {
        if (!status.canMoveTo(next)) {
            throw new IllegalStateException(status + " -> " + next + " is not allowed");
        }
        this.status = next;
    }

    public Long getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}