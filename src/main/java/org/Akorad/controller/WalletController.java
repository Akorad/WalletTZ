package org.Akorad.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.wallet.OperationRequest;
import org.Akorad.dto.wallet.TransferDTO;
import org.Akorad.dto.wallet.WalletDto;
import org.Akorad.dto.wallet.WalletResponseDTO;
import org.Akorad.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletResponseDTO> createWallet() {
        WalletResponseDTO wallet = walletService.createWallet();
        return ResponseEntity.ok(wallet);
    }

    @DeleteMapping("/delete/{walletId}")
    public ResponseEntity<Void> deleteWallet(@NotNull @PathVariable UUID walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getBalance/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@NotNull @PathVariable UUID walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @PostMapping
    public ResponseEntity<Void> processOperation(@Valid @RequestBody OperationRequest operationRequest) {
        walletService.processOperation(operationRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferDTO transferDTO) {
        walletService.transfer(transferDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getUserWallets")
    public ResponseEntity<Page<WalletDto>> getWallets(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<WalletDto> wallets = walletService.getUserWallets(Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(wallets);
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<Page<TransactionDto>> getTransactions (@NotNull @PathVariable UUID walletId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<TransactionDto> transactions = walletService.getTransactionsByWalletId(walletId, Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(transactions);
    }
}
