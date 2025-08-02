package org.Akorad.wallets.service;

import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.transaction.TransactionMapper;
import org.Akorad.entity.Transaction;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.transaction.InsufficientFundsException;
import org.Akorad.exception.validator.InvalidAmountException;
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

    @Test
    void deposit_ShouldIncreaseBalance_WhenAmountIsValid() {
        BigDecimal amount = BigDecimal.valueOf(50);
        String comment = "Test deposit";

        // Валидатор пропускает
        doNothing().when(operationValidator).validateAmount(amount);
        // Возвращаем кошелек при поиске
        when(operationValidator.findWalletOrTrow(walletId, walletRepository)).thenReturn(wallet);
        // Сохраняем кошелек
        when(walletRepository.save(wallet)).thenReturn(wallet);
        // Сохраняем транзакцию
        when(transactionalRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.deposit(walletId, amount, comment);

        // Баланс должен увеличиться
        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());

        // Проверяем вызовы репозиториев
        verify(walletRepository).save(wallet);
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
        BigDecimal amount = BigDecimal.valueOf(50);
        String comment = "withdraw test";

        doNothing().when(operationValidator).validateAmount(amount);
        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(transactionalRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.withdraw(wallet, amount, comment);

        assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
        verify(walletRepository).save(wallet);
        verify(transactionalRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_ShouldThrowInsufficientFundsException_WhenNotEnoughBalance() {
        BigDecimal amount = BigDecimal.valueOf(150);
        String comment = "withdraw too much";

        doNothing().when(operationValidator).validateAmount(amount);

        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class,
                () -> transactionService.withdraw(wallet, amount, comment));

        assertTrue(ex.getMessage().contains(walletId.toString()));
    }

    @Test
    void transfer_ShouldMoveFunds_WhenSufficientBalance() {
        Wallet toWallet = new Wallet();
        toWallet.setWalletId(UUID.randomUUID());
        toWallet.setBalance(BigDecimal.valueOf(20));

        BigDecimal amount = BigDecimal.valueOf(30);
        String comment = "transfer test";

        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionalRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        transactionService.transfer(wallet, toWallet, amount, comment);

        assertEquals(BigDecimal.valueOf(70), wallet.getBalance());
        assertEquals(BigDecimal.valueOf(50), toWallet.getBalance());

        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionalRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_ShouldThrowInsufficientFundsException_WhenNotEnoughBalance() {
        Wallet toWallet = new Wallet();
        toWallet.setWalletId(UUID.randomUUID());
        toWallet.setBalance(BigDecimal.valueOf(20));

        BigDecimal amount = BigDecimal.valueOf(150);

        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(wallet, toWallet, amount, "too much"));

        assertTrue(ex.getMessage().contains(wallet.getWalletId().toString()));
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
