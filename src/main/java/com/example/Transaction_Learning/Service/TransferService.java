package com.example.Transaction_Learning.Service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Transaction_Learning.DTO.TransferRequest;
import com.example.Transaction_Learning.Exceptions.InsufficientFundsException;
import com.example.Transaction_Learning.Exceptions.InvalidTransferException;
import com.example.Transaction_Learning.Model.Account;
import com.example.Transaction_Learning.Model.TransferRecord;
import com.example.Transaction_Learning.Repo.AccountRepository;
import com.example.Transaction_Learning.Repo.TransferRecordRepository;

/**
 * Core transactional service that debits the sender, credits the receiver, and
 * persists a {@link TransferRecord}.
 *
 * <p>This class should NOT be called directly from the controller; use
 * {@link TransferFacade} (which implements {@link TransferHandler}) to get
 * idempotency handling on top.
 */
@Service
public class TransferService {

    private final AccountRepository accounts;
    private final TransferRecordRepository records;

    public TransferService(
            AccountRepository accounts,
            TransferRecordRepository records) {
        this.accounts = accounts;
        this.records = records;
    }

    /**
     * Executes the transfer inside a single database transaction.
     *
     * @param key  idempotency key forwarded from the HTTP header
     * @param hash SHA-256 fingerprint of the request body used for duplicate detection
     * @param req  the validated transfer request
     * @return the newly created (and completed) {@link TransferRecord}
     */
    @Transactional
    public TransferRecord transfer(
            String key,
            String hash,
            TransferRequest req) {

        // 1. Validate the request
        if (req.fromId().equals(req.toId())) {
            throw new InvalidTransferException(
                    "Source and destination accounts must be different");
        }

        // 2. Check whether this request was already processed (race-condition guard
        //    inside the same transaction — the outer facade handles the common path).
        var existing = records.findByIdempotencyKey(key);

        if (existing.isPresent()) {
            TransferRecord previous = existing.get();

            if (!Objects.equals(previous.getRequestHash(), hash)) {
                throw new InvalidTransferException(
                        "Idempotency key reused with a different request");
            }

            return previous;
        }

        // 3. Acquire pessimistic locks in a deterministic order to avoid deadlocks.
        Long first  = Math.min(req.fromId(), req.toId());
        Long second = Math.max(req.fromId(), req.toId());

        Account a1 = accounts.findByIdForUpdate(first)
                .orElseThrow(() ->
                        new InvalidTransferException(
                                "Account not found: " + first));

        Account a2 = accounts.findByIdForUpdate(second)
                .orElseThrow(() ->
                        new InvalidTransferException(
                                "Account not found: " + second));

        Account from = req.fromId().equals(first) ? a1 : a2;
        Account to   = req.toId().equals(first)   ? a1 : a2;

        // 4. Check balance before mutating.
        if (from.getBalance().compareTo(req.amount()) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds in source account");
        }

        // 5. Mutate balances.
        from.debit(req.amount());
        to.credit(req.amount());

        // 6. Persist the transfer record and mark it completed.
        TransferRecord record = records.saveAndFlush(
                new TransferRecord(
                        key,
                        hash,
                        req.fromId(),
                        req.toId(),
                        req.amount()));

        record.complete();

        return record;
    }
}