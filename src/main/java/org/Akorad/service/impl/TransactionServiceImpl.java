package org.Akorad.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.transaction.TransactionMapper;
import org.Akorad.entity.Transaction;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.transaction.InsufficientFundsException;
import org.Akorad.exception.wallet.WalletNotFoundException;
import org.Akorad.reposetory.TransactionalRepository;
import org.Akorad.reposetory.WalletRepository;
import org.Akorad.service.TransactionService;
import org.Akorad.util.OperationValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionalRepository transactionalRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;
    private final OperationValidator operationValidator;
    private final EntityManager entityManager;

    @Override
    public void deposit(UUID walletId, BigDecimal amount, String comment) {
        executeWalletOperation(walletId, amount, comment, this::performDeposit);
    }

    @Override
    public void withdraw(Wallet wallet, BigDecimal amount, String comment) {
        executeWalletOperation(wallet.getWalletId(), amount, comment, this::performWithdraw);
    }

    @Override
    public void transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment) {
        transfer(fromWallet.getWalletId(), toWallet.getWalletId(), amount, comment);
    }

    // Новый вспомогательный метод для UUID-версии
    private void transfer(UUID fromWalletId, UUID toWalletId, BigDecimal amount, String comment) {
        operationValidator.validateAmount(amount);
        validateSameWallet(fromWalletId, toWalletId);

        UUID firstId = fromWalletId.compareTo(toWalletId) < 0 ? fromWalletId : toWalletId;
        UUID secondId = fromWalletId.compareTo(toWalletId) < 0 ? toWalletId : fromWalletId;

        Wallet firstWallet = lockAndGetWallet(firstId);
        Wallet secondWallet = lockAndGetWallet(secondId);

        Wallet fromWallet = fromWalletId.equals(firstId) ? firstWallet : secondWallet;
        Wallet toWallet = toWalletId.equals(secondId) ? secondWallet : firstWallet;

        performTransfer(fromWallet, toWallet, amount, comment);
    }

    @Override
    public Page<TransactionDto> getTransactionsByWalletId(Wallet wallet, Pageable pageable) {
        Page<Transaction> transactions = transactionalRepository.findAllByWallet(wallet, pageable);
        return transactions.map(transactionMapper::toDto);
    }

    // ============== PRIVATE METHODS ============== //

    private Wallet lockAndGetWallet(UUID walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        entityManager.lock(wallet, LockModeType.PESSIMISTIC_WRITE);
        entityManager.refresh(wallet);
        return wallet;
    }

    private void executeWalletOperation(UUID walletId, BigDecimal amount, String comment,
                                        BiConsumer<Wallet, BigDecimal> operation) {
        operationValidator.validateAmount(amount);
        Wallet wallet = lockAndGetWallet(walletId);
        operation.accept(wallet, amount);
        createTransaction(wallet, amount, comment);
    }

    private void performDeposit(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
    }

    private void performWithdraw(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(wallet.getWalletId(), amount);
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
    }

    private void performTransfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment) {
        performWithdraw(fromWallet, amount);
        performDeposit(toWallet, amount);
        createTransferTransaction(fromWallet, toWallet, amount, comment);
    }

    private void createTransaction(Wallet wallet, BigDecimal amount, String comment) {
        Transaction transaction = Transaction.deposit(wallet, amount, comment);
        transactionalRepository.save(transaction);
    }

    private void createTransferTransaction(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment) {
        Transaction transaction = Transaction.transfer(fromWallet, toWallet, amount, comment);
        transactionalRepository.save(transaction);
    }

    private void validateSameWallet(UUID id1, UUID id2) {
        if (id1.equals(id2)) {
            throw new IllegalArgumentException("Невозможно перевести деньги на один и тот же кошелек");
        }
    }

}