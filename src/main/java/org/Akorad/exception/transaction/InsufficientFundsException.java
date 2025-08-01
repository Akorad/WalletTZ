package org.Akorad.exception.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID id, BigDecimal amount) {
        super("Недостаточно средств на кошельке " + id + " для списания " + amount);
    }
}
