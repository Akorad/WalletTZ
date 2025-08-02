package org.Akorad.wallets.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.transaction.TransactionMapper;
import org.Akorad.entity.Transaction;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.transaction.InsufficientFundsException;
import org.Akorad.exception.validator.InvalidAmountException;
import org.Akorad.exception.wallet.WalletNotFoundException;
import org.Akorad.reposetory.TransactionalRepository;
import org.Akorad.reposetory.WalletRepository;
import org.Akorad.service.impl.TransactionServiceImpl;
import org.Akorad.util.OperationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionalRepository transactionalRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private OperationValidator operationValidator;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Wallet wallet;

    private UUID walletId;

    @BeforeEach
    void setup() {
        walletId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(BigDecimal.valueOf(100));
        wallet.setVersion(1);
    }

    private Wallet createWallet(UUID id, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setWalletId(id);
        wallet.setBalance(balance);
        return wallet;
    }

    @Test
    void deposit_ShouldIncreaseBalance_WhenAmountIsValid() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100);
        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(wallet));

        // Act
        transactionService.deposit(walletId, amount, "Test deposit");

        // Assert
        assertEquals(BigDecimal.valueOf(200), wallet.getBalance());
        verify(walletRepository).findByWalletId(walletId);
        verify(entityManager).lock(wallet, LockModeType.PESSIMISTIC_WRITE);
        verify(entityManager).refresh(wallet);
        verify(transactionalRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_ShouldThrowInvalidAmountException_WhenAmountIsNegative() {
        BigDecimal amount = BigDecimal.valueOf(-10);

        doThrow(new InvalidAmountException("Сумма должна быть положительной"))
                .when(operationValidator).validateAmount(amount);

        assertThrows(InvalidAmountException.class,
                () -> transactionService.deposit(walletId, amount, "invalid deposit"));
    }

    @Test
    void withdraw_ShouldDecreaseBalance_WhenSufficientFunds() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100);
        Wallet wallet = createWallet(walletId, BigDecimal.valueOf(1000));
        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(wallet));

        // Act
        transactionService.withdraw(wallet, amount, "Test withdraw");

        // Assert
        assertEquals(BigDecimal.valueOf(900), wallet.getBalance());
        verify(walletRepository).findByWalletId(walletId);
        verify(entityManager).lock(wallet, LockModeType.PESSIMISTIC_WRITE);
        verify(entityManager).refresh(wallet);
        verify(transactionalRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_ShouldThrowException_WhenInsufficientFunds() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(1500);
        Wallet wallet = createWallet(walletId, BigDecimal.valueOf(1000));
        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () ->
                transactionService.withdraw(wallet, amount, "Test withdraw"));
    }

    @Test
    void transfer_ShouldMoveFunds_WhenSufficientBalance() {
        // Arrange
        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();

        Wallet fromWallet = createWallet(fromWalletId, BigDecimal.valueOf(1000));
        Wallet toWallet = createWallet(toWalletId, BigDecimal.ZERO);

        when(walletRepository.findByWalletId(fromWalletId)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findByWalletId(toWalletId)).thenReturn(Optional.of(toWallet));

        // Act
        transactionService.transfer(fromWallet, toWallet, BigDecimal.valueOf(500), "Test transfer");

        // Assert
        assertEquals(BigDecimal.valueOf(500), fromWallet.getBalance());
        assertEquals(BigDecimal.valueOf(500), toWallet.getBalance());
        verify(transactionalRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_ShouldThrowException_WhenWalletNotFound() {
        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () ->
                transactionService.deposit(walletId, BigDecimal.TEN, "Test"));
    }

    @Test
    void getTransactionsByWalletId_ShouldReturnMappedPage() {
        Pageable pageable = Pageable.unpaged();

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setComment("comment");

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionalRepository.findAllByWallet(wallet, pageable)).thenReturn(transactionPage);

        TransactionDto dto = new TransactionDto();
        dto.setWalletId(wallet.getWalletId());
        dto.setAmount(BigDecimal.TEN);
        dto.setComment("comment");

        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        Page<TransactionDto> result = transactionService.getTransactionsByWalletId(wallet, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().getFirst());
    }
}
