package com.example.Transaction_Learning.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.Transaction_Learning.Command.TransferCommand;
import com.example.Transaction_Learning.DTO.TransferRequest;
import com.example.Transaction_Learning.Idempotency.IdempotencyConflictException;
import com.example.Transaction_Learning.Model.TransferRecord;
import com.example.Transaction_Learning.Repo.TransferRecordRepository;

/**
 * Orchestrates idempotency checking and delegates the actual transfer logic to
 * {@link TransferService}.
 *
 * <p>This class implements {@link TransferHandler} so the controller has a single,
 * stable dependency.
 */
@Service
public class TransferFacade implements TransferHandler {

    private final TransferService service;
    private final TransferRecordRepository records;

    public TransferFacade(
            TransferService service,
            TransferRecordRepository records) {
        this.service = service;
        this.records = records;
    }

    /**
     * Entry-point called by the controller.
     * Converts the command into a {@link TransferRequest}, computes a request hash,
     * and delegates to {@link #execute(String, TransferRequest)} with idempotency handling.
     */
    @Override
    public TransferRecord handle(TransferCommand command) {
        TransferRequest req = new TransferRequest(
                command.fromId(),
                command.toId(),
                command.amount());
        return execute(command.idempotencyKey(), req);
    }

    /**
     * Executes the transfer with idempotency protection.
     */
    public TransferRecord execute(String key, TransferRequest req) {

        String hash = sha256(
                req.fromId() + ":" +
                req.toId() + ":" +
                req.amount().toPlainString()
        );

        Optional<TransferRecord> existing =
                records.findByIdempotencyKey(key);

        if (existing.isPresent()) {
            return validate(existing.get(), hash);
        }

        try {
            return service.transfer(key, hash, req);

        } catch (DataIntegrityViolationException dup) {

            // A concurrent request may have inserted the same key.
            // Retrieve its result after the failed transaction has ended.
            TransferRecord existingRecord =
                    records.findByIdempotencyKey(key)
                            .orElseThrow(() -> dup);

            return validate(existingRecord, hash);
        }
    }

    private TransferRecord validate(
            TransferRecord record,
            String hash) {

        if (!Objects.equals(record.getRequestHash(), hash)) {
            throw new IdempotencyConflictException(
                    "Idempotency key reused with a different request"
            );
        }

        return record;
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable", e
            );
        }
    }
}