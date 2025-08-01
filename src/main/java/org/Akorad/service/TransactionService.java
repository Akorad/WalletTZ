package org.Akorad.service;

import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {

    void deposit(UUID walletID, BigDecimal amount, String comment);

    void withdraw(Wallet wallet, BigDecimal amount, String comment);

    void transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment);

    Page<TransactionDto> getTransactionsByWalletId(Wallet wallet, Pageable pageable);
}
