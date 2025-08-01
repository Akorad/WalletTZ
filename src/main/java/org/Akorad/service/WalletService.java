package org.Akorad.service;

import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.wallet.OperationRequest;
import org.Akorad.dto.wallet.TransferDTO;
import org.Akorad.dto.wallet.WalletDto;
import org.Akorad.dto.wallet.WalletResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    WalletResponseDTO createWallet();

    void deleteWallet(UUID walletId);

    Page<WalletDto> getUserWallets(Pageable pageable);

    BigDecimal getBalance(UUID walletId);

    void deposit(UUID walletId, BigDecimal amount, String comment);

    void withdraw(UUID walletId, BigDecimal amount, String comment);

    void transfer(TransferDTO transferDTO);

    void processOperation(OperationRequest request);

    Page<TransactionDto> getTransactionsByWalletId(UUID walletId, Pageable pageable);
}
