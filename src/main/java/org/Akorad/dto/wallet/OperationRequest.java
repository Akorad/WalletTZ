package org.Akorad.dto.wallet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.Akorad.entity.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Запрос на операцию с кошельком")
public class OperationRequest {

    @NotNull
    @Schema(description = "ID кошелька", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID walletId;

    @NotNull
    @Schema(description = "Тип операции", required = true, example = "DEPOSIT")
    private OperationType operationType;

    @NotNull
    @Positive
    @DecimalMin("0.01")
    @Schema(description = "Сумма операции", required = true, example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Комментарий к операции", example = "Пополнение счета")
    private String comment;
}
