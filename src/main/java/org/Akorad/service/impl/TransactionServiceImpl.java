package org.Akorad.service.impl;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.transaction.TransactionMapper;
import org.Akorad.entity.Transaction;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.transaction.InsufficientFundsException;
import org.Akorad.exception.transaction.InvalidTransactionException;
import org.Akorad.reposetory.TransactionalRepository;
import org.Akorad.reposetory.WalletRepository;
import org.Akorad.service.TransactionService;
import org.Akorad.util.OperationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionalRepository transactionalRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;
    private final OperationValidator operationValidator;

    @Override
    public void deposit(UUID walletID, BigDecimal amount, String comment) {
        executeWithRetry(()-> {
            operationValidator.validateAmount(amount);

            Wallet wallet = operationValidator.findWalletOrTrow(walletID, walletRepository);

            wallet.setBalance(wallet.getBalance().add(amount));

            walletRepository.save(wallet);

             return transactionalRepository.save(Transaction.deposit(wallet, amount, comment));
        }, 5);

    }

    @Override
    public void withdraw(Wallet wallet, BigDecimal amount, String comme) {
        executeWithRetry(()-> {
            operationValidator.validateAmount(amount);

            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(wallet.getWalletId(), amount);
            }

            wallet.setBalance(wallet.getBalance().subtract(amount));

            walletRepository.save(wallet);

            return transactionalRepository.save(Transaction.withdraw(wallet, amount, comme));
        }, 5);
    }

    @Override
    public void transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String comment) {
        executeWithRetry(()-> {
            if (fromWallet.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(fromWallet.getWalletId(), amount);
            }

            fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
            toWallet.setBalance(toWallet.getBalance().add(amount));

            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            return transactionalRepository.save(Transaction.transfer(fromWallet, toWallet, amount, comment));
        }, 3);
    }

    @Override
    public Page<TransactionDto> getTransactionsByWalletId(Wallet wallet, Pageable pageable) {
        Page<Transaction> transactions = transactionalRepository.findAllByWallet(wallet, pageable);
        return transactions.map(transactionMapper::toDto);
    }

    private <T> T executeWithRetry(Supplier<T> action, int maxRetries) {
        int attempts = 0;
        while (true) {
            try {
                return action.get();
            } catch (OptimisticLockException e) {
                if (++attempts > maxRetries) {
                    throw new InvalidTransactionException("Превышено максимальное количество попыток транзакции");
                }
            }
        }
    }
}