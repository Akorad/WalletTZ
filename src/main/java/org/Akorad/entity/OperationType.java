package org.Akorad.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Типы операций с кошельком")
public enum OperationType {
    @Schema(description = "Пополнение кошелька")
    DEPOSIT,

    @Schema(description = "Снятие средств с кошелька")
    WITHDRAWAL,

    @Schema(description = "Перевод средств между кошельками")
    TRANSFER
}
