package com.example.Transaction_Learning.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Transaction_Learning.DTO.TransferRequest;
import com.example.Transaction_Learning.DTO.TransferResponse;
import com.example.Transaction_Learning.Model.TransferRecord;
import com.example.Transaction_Learning.Service.TransferHandler;
import com.example.Transaction_Learning.Command.TransferCommand;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferHandler handler;

    public TransferController(TransferHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> create(
            @RequestHeader("Idempotency-Key") String key,
            @Valid @RequestBody TransferRequest req) {

        var cmd = new TransferCommand(key, req.fromId(), req.toId(), req.amount());
        TransferRecord record = handler.handle(cmd);
        return ResponseEntity.ok(TransferResponse.from(record));
    }
}