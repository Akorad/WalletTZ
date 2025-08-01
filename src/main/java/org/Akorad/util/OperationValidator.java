package org.Akorad.util;

import org.Akorad.entity.User;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.validator.InvalidAmountException;
import org.Akorad.exception.wallet.WalletNotFoundException;
import org.Akorad.exception.wallet.WalletOrUserNotFoundException;
import org.Akorad.reposetory.WalletRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class OperationValidator {

    public Wallet findUserWalletOrThrow(UUID walletId, User user, WalletRepository walletRepository) {
        return walletRepository.findByWalletIdAndUserId(walletId, user.getId())
                .orElseThrow(() -> new WalletOrUserNotFoundException(walletId));
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Сумма должна быть положительной");
        }
    }

    public Wallet findWalletOrTrow (UUID walletID, WalletRepository walletRepository) {
        return walletRepository.findWalletByWalletId(walletID)
                .orElseThrow(() -> new WalletNotFoundException(walletID));
    }
}
