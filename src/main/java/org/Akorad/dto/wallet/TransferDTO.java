package org.Akorad.dto.wallet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Данные для перевода между кошельками")
public class TransferDTO {
    @NotNull
    @Schema(description = "ID кошелька-отправителя", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID fromWalletId;

    @NotNull
    @Schema(description = "ID кошелька-получателя", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID toWalletId;

    @NotNull
    @Positive
    @DecimalMin("0.01")
    @Schema(description = "Сумма перевода", required = true, example = "250.00")
    private BigDecimal amount;

    @Schema(description = "Комментарий к переводу", example = "Перевод на оплату услуг")
    private String comment;
}
