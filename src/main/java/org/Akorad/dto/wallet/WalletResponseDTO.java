package org.Akorad.dto.wallet;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletResponseDTO {
    private UUID walletId;
    private BigDecimal balance;
    private String ownerName;
}
