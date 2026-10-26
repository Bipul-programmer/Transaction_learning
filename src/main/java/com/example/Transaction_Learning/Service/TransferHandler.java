package com.example.Transaction_Learning.Service;

import com.example.Transaction_Learning.Command.TransferCommand;
import com.example.Transaction_Learning.Model.TransferRecord;

/**
 * Primary entry-point for executing a transfer.
 * The controller depends on this interface; {@link TransferFacade} is the implementation.
 */
public interface TransferHandler {
    TransferRecord handle(TransferCommand command);
}
