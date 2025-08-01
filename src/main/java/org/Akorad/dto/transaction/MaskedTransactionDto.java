package org.Akorad.dto.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaskedTransactionDto {
    private LocalDateTime date;
    private BigDecimal amount;
    private String direction; // "INCOMING" or "OUTGOING"
    private String counterparty; // имя отправителя/получателя (маскировано)
}
