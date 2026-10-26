package com.example.Transaction_Learning.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Transaction_Learning.Model.TransferRecord;

public interface TransferRecordRepository
        extends JpaRepository<TransferRecord, Long> {

    Optional<TransferRecord> findByIdempotencyKey(String idempotencyKey);
}