package org.Akorad.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.Akorad.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Информация о транзакции")
public class TransactionDto {
    @Schema(description = "ID кошелька, к которому относится транзакция", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID walletId;

    @Schema(description = "Тип операции", example = "WITHDRAW")
    private OperationType operationType;

    @Schema(description = "Сумма операции", example = "500.00")
    private BigDecimal amount;

    @Schema(description = "Дата и время транзакции", example = "2025-08-02T12:34:56")
    private LocalDateTime transactionDate;

    @Schema(description = "Комментарий к транзакции", example = "Оплата товаров")
    private String comment;
}