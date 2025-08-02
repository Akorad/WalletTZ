package org.Akorad.dto.wallet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о кошельке")
public class WalletDto {
    @Schema(description = "Уникальный идентификатор кошелька", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID walletId;

    @Schema(description = "Баланс кошелька", example = "1500.75")
    private BigDecimal balance;
}
