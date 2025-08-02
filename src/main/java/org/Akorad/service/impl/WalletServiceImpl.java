package org.Akorad.service.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.wallet.*;
import org.Akorad.entity.User;
import org.Akorad.entity.Wallet;
import org.Akorad.exception.wallet.CannotDeleteLastWalletException;
import org.Akorad.reposetory.WalletRepository;
import org.Akorad.service.TransactionService;
import org.Akorad.service.UserService;
import org.Akorad.service.WalletService;
import org.Akorad.util.OperationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional()
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;
    private final OperationValidator operationValidator;


    @Override
    public WalletResponseDTO createWallet() {
        User currentUser = userService.getCurrentUser();

        Wallet wallet = new Wallet();
        wallet.setWalletId(UUID.randomUUID());
        wallet.setUser(currentUser);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);

        return walletMapper.toDto(wallet);
    }

    @Override
    public void deleteWallet(UUID walletId) {
        User user = userService.getCurrentUser();
        Wallet wallet = operationValidator.findUserWalletOrThrow(walletId, user, walletRepository);

        List<Wallet> otherWallets = walletRepository.findAllByUserId(user.getId())
                .stream()
                .filter(w -> !w.getWalletId().equals(walletId))
                .toList();

        if (otherWallets.isEmpty()) {
            throw new CannotDeleteLastWalletException();
        }

        if (wallet.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            Wallet targetWallet = otherWallets.getFirst();
            transactionService.transfer(wallet,
                    targetWallet,
                    wallet.getBalance(),
                    "Перевод остатков при удалении кошелька");
        }

        walletRepository.delete(wallet);
    }

    @Override
    public Page<WalletDto> getUserWallets(Pageable pageable) {
        User user = userService.getCurrentUser();

        return walletRepository.findAllByUserId(user.getId(), pageable)
                .map(walletMapper::toWalletDto);
    }

    @Override
    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = operationValidator.findUserWalletOrThrow(walletId, userService.getCurrentUser(), walletRepository);
        return wallet.getBalance();
    }

    @Override
    public void deposit(UUID walletId, BigDecimal amount, String comment) {
        operationValidator.validateAmount(amount);

        transactionService.deposit(walletId, amount, comment);
    }

    @Override
    public void withdraw(UUID walletId, BigDecimal amount, String comment) {
        operationValidator.validateAmount(amount);

        Wallet wallet = operationValidator.findUserWalletOrThrow(walletId, userService.getCurrentUser(), walletRepository);

        transactionService.withdraw(wallet, amount, comment);
    }

    @Override
    public void transfer(TransferDTO transferDTO) {
        operationValidator.validateAmount(transferDTO.getAmount());

        User user = userService.getCurrentUser();

        Wallet fromWallet = operationValidator.findUserWalletOrThrow(transferDTO.getFromWalletId(), user, walletRepository);
        Wallet toWallet = operationValidator.findWalletOrTrow(transferDTO.getToWalletId(), walletRepository);

        if (fromWallet.equals(toWallet)) {
            throw new IllegalArgumentException("Нельзя перевести средства на тот же кошелек");
        }

        transactionService.transfer(fromWallet, toWallet, transferDTO.getAmount(), transferDTO.getComment());
    }

    @Override
    public Page<TransactionDto> getTransactionsByWalletId(UUID walletId, Pageable pageable) {
        Wallet wallet = operationValidator.findUserWalletOrThrow(walletId, userService.getCurrentUser(), walletRepository);
        return transactionService.getTransactionsByWalletId(wallet, pageable);
    }

    @Override
    public void processOperation(OperationRequest request) {
        User user = userService.getCurrentUser();
        Wallet wallet = operationValidator.findUserWalletOrThrow(request.getWalletId(), user, walletRepository);

        switch (request.getOperationType()) {
            case DEPOSIT -> deposit(wallet.getWalletId(), request.getAmount(), request.getComment());
            case WITHDRAWAL -> withdraw(wallet.getWalletId(), request.getAmount(), request.getComment());
        }
    }
}