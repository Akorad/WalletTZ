package org.Akorad.exception.wallet;

import java.util.UUID;

public class WalletOrUserNotFoundException extends RuntimeException{
    public WalletOrUserNotFoundException(UUID uuid) {
        super("На вашем аккаунте кошелек с UUID " + uuid + " не найден");
    }
}
