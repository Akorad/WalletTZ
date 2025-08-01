package org.Akorad.dto.transaction;

import lombok.Data;
import org.Akorad.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDto {
    private UUID walletId;
    private OperationType operationType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String comment;
}
