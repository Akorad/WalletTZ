package org.Akorad.exception.wallet;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(UUID uuid) {
        super("Кошелек с UUID " + uuid + " не найден");
    }
}
