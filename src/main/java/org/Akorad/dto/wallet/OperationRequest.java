package org.Akorad.dto.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.Akorad.entity.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    @Positive
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String comment;
}
