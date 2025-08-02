package org.Akorad.wallets.service;

import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.wallet.*;
import org.Akorad.entity.OperationType;
import org.Akorad.entity.User;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.wallet.CannotDeleteLastWalletException;
import org.Akorad.reposetory.WalletRepository;
import org.Akorad.service.TransactionService;
import org.Akorad.service.UserService;
import org.Akorad.service.impl.WalletServiceImpl;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    UserService userService;

    @Mock
    TransactionService transactionService;

    @Mock
    WalletMapper walletMapper;

    @Mock
    OperationValidator operationValidator;

    @InjectMocks
    WalletServiceImpl walletService;

    User testUser;
    Wallet wallet1;
    Wallet wallet2;

    UUID walletId1 = UUID.randomUUID();
    UUID walletId2 = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("user1")
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        wallet1 = new Wallet();
        wallet1.setId(1L);
        wallet1.setWalletId(walletId1);
        wallet1.setUser(testUser);
        wallet1.setBalance(BigDecimal.valueOf(100));

        wallet2 = new Wallet();
        wallet2.setId(2L);
        wallet2.setWalletId(walletId2);
        wallet2.setUser(testUser);
        wallet2.setBalance(BigDecimal.ZERO);
    }

    // --- Тест createWallet ---

    @Test
    void createWallet_ShouldCreateAndReturnWalletResponseDTO() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(walletMapper.toDto(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet w = invocation.getArgument(0);
            WalletResponseDTO dto = new WalletResponseDTO();
            dto.setWalletId(w.getWalletId());
            dto.setBalance(w.getBalance());
            dto.setOwnerName(w.getUser().getFirstName() + " " + w.getUser().getLastName());
            return dto;
        });

        WalletResponseDTO response = walletService.createWallet();

        assertNotNull(response);
        assertEquals(testUser.getFirstName() + " " + testUser.getLastName(), response.getOwnerName());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        verify(walletRepository).save(any(Wallet.class));
    }

    // --- Тест deleteWallet ---

    @Test
    void deleteWallet_ShouldThrow_WhenDeletingLastWallet() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        when(walletRepository.findAllByUserId(testUser.getId())).thenReturn(List.of(wallet1));

        CannotDeleteLastWalletException ex = assertThrows(CannotDeleteLastWalletException.class, () ->
                walletService.deleteWallet(walletId1));

        assertEquals("Невозможно удалить последний кошелек. Создайте новый кошелек перед удалением текущего.", ex.getMessage());
    }

    @Test
    void deleteWallet_ShouldTransferBalanceAndDelete_WhenBalanceGreaterThanZero() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        when(walletRepository.findAllByUserId(testUser.getId())).thenReturn(List.of(wallet1, wallet2));

        walletService.deleteWallet(walletId1);

        verify(transactionService).transfer(eq(wallet1), eq(wallet2), eq(wallet1.getBalance()), anyString());
        verify(walletRepository).delete(wallet1);
    }

    @Test
    void deleteWallet_ShouldDeleteWithoutTransfer_WhenBalanceIsZero() {
        wallet1.setBalance(BigDecimal.ZERO);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        when(walletRepository.findAllByUserId(testUser.getId())).thenReturn(List.of(wallet1, wallet2));

        walletService.deleteWallet(walletId1);

        verify(transactionService, never()).transfer(any(), any(), any(), anyString());
        verify(walletRepository).delete(wallet1);
    }

    // --- Тест getUserWallets ---

    @Test
    void getUserWallets_ShouldReturnPagedWalletDto() {
        Pageable pageable = Pageable.unpaged();
        Page<Wallet> walletPage = new PageImpl<>(List.of(wallet1, wallet2));

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(walletRepository.findAllByUserId(testUser.getId(), pageable)).thenReturn(walletPage);
        when(walletMapper.toWalletDto(wallet1)).thenReturn(new WalletDto(walletId1, wallet1.getBalance()));
        when(walletMapper.toWalletDto(wallet2)).thenReturn(new WalletDto(walletId2, wallet2.getBalance()));

        Page<WalletDto> result = walletService.getUserWallets(pageable);

        assertEquals(2, result.getTotalElements());
    }

    // --- Тест getBalance ---

    @Test
    void getBalance_ShouldReturnWalletBalance() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);

        BigDecimal balance = walletService.getBalance(walletId1);

        assertEquals(wallet1.getBalance(), balance);
    }

    // --- Тест deposit ---

    @Test
    void deposit_ShouldCallTransactionServiceDeposit() {
        BigDecimal amount = BigDecimal.valueOf(50);
        String comment = "Пополнение";

        // Проверка amount проходит в валидаторе (не мокается здесь)
        doNothing().when(operationValidator).validateAmount(amount);

        walletService.deposit(walletId1, amount, comment);

        verify(transactionService).deposit(walletId1, amount, comment);
    }

    @Test
    void deposit_ShouldThrow_WhenAmountInvalid() {
        BigDecimal amount = BigDecimal.ZERO;
        doThrow(new RuntimeException("Invalid amount")).when(operationValidator).validateAmount(amount);

        assertThrows(RuntimeException.class, () -> walletService.deposit(walletId1, amount, "test"));
    }

    // --- Тест withdraw ---

    @Test
    void withdraw_ShouldCallTransactionServiceWithdraw() {
        BigDecimal amount = BigDecimal.valueOf(30);
        String comment = "Снятие";

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        doNothing().when(operationValidator).validateAmount(amount);

        walletService.withdraw(walletId1, amount, comment);

        verify(transactionService).withdraw(wallet1, amount, comment);
    }

    // --- Тест transfer ---

    @Test
    void transfer_ShouldCallTransactionServiceTransfer() {
        BigDecimal amount = BigDecimal.valueOf(20);
        String comment = "Перевод";

        TransferDTO dto = new TransferDTO();
        dto.setFromWalletId(walletId1);
        dto.setToWalletId(walletId2);
        dto.setAmount(amount);
        dto.setComment(comment);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        when(operationValidator.findWalletOrTrow(walletId2, walletRepository)).thenReturn(wallet2);
        doNothing().when(operationValidator).validateAmount(amount);

        walletService.transfer(dto);

        verify(transactionService).transfer(wallet1, wallet2, amount, comment);
    }

    @Test
    void transfer_ShouldThrow_WhenFromAndToWalletAreSame() {
        BigDecimal amount = BigDecimal.valueOf(20);

        TransferDTO dto = new TransferDTO();
        dto.setFromWalletId(walletId1);
        dto.setToWalletId(walletId1);
        dto.setAmount(amount);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        when(operationValidator.findWalletOrTrow(walletId1, walletRepository)).thenReturn(wallet1);
        doNothing().when(operationValidator).validateAmount(amount);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> walletService.transfer(dto));
        assertEquals("Нельзя перевести средства на тот же кошелек", ex.getMessage());
    }

    // --- Тест getTransactionsByWalletId ---

    @Test
    void getTransactionsByWalletId_ShouldReturnPageOfTransactionDto() {
        Pageable pageable = Pageable.unpaged();

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);

        Page<TransactionDto> transactionPage = new PageImpl<>(List.of());
        when(transactionService.getTransactionsByWalletId(wallet1, pageable)).thenReturn(transactionPage);

        Page<TransactionDto> result = walletService.getTransactionsByWalletId(walletId1, pageable);

        assertNotNull(result);
        verify(transactionService).getTransactionsByWalletId(wallet1, pageable);
    }

    // --- Тест processOperation ---

    @Test
    void processOperation_ShouldCallDeposit_WhenOperationIsDeposit() {
        OperationRequest request = new OperationRequest();
        request.setWalletId(walletId1);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(50));
        request.setComment("Пополнение");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        doNothing().when(operationValidator).validateAmount(request.getAmount());

        walletService.processOperation(request);

        verify(transactionService).deposit(walletId1, request.getAmount(), request.getComment());
    }

    @Test
    void processOperation_ShouldCallWithdraw_WhenOperationIsWithdrawal() {
        OperationRequest request = new OperationRequest();
        request.setWalletId(walletId1);
        request.setOperationType(OperationType.WITHDRAWAL);
        request.setAmount(BigDecimal.valueOf(50));
        request.setComment("Снятие");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(operationValidator.findUserWalletOrThrow(walletId1, testUser, walletRepository)).thenReturn(wallet1);
        doNothing().when(operationValidator).validateAmount(request.getAmount());

        walletService.processOperation(request);

        verify(transactionService).withdraw(wallet1, request.getAmount(), request.getComment());
    }
}

