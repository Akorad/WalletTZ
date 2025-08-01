package org.Akorad.dto.wallet;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletDto {
    private UUID walletId;
    private BigDecimal balance;
}
