package org.Akorad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

    @Operation(summary = "Создать новый кошелек", description = "Создает пустой кошелек для текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Кошелек успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WalletResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<WalletResponseDTO> createWallet() {
        WalletResponseDTO wallet = walletService.createWallet();
        return ResponseEntity.ok(wallet);
    }

    @Operation(summary = "Удалить кошелек", description = "Удаляет кошелек по его ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Кошелек успешно удален", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/delete/{walletId}")
    public ResponseEntity<Void> deleteWallet(
            @Parameter(description = "ID кошелька", required = true, in = ParameterIn.PATH)
            @PathVariable(name = "walletId") UUID walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить баланс кошелька", description = "Возвращает текущий баланс указанного кошелька.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно получен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден", content = @Content)
    })
    @GetMapping("/getBalance/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "ID кошелька", required = true, in = ParameterIn.PATH)
            @PathVariable(name = "walletId") UUID walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @Operation(summary = "Выполнить операцию с кошельком", description = "Выполняет операцию (пополнение, списание) с кошельком.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция выполнена", content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> processOperation(
            @Parameter(description = "Данные операции", required = true)
            @Valid @RequestBody OperationRequest operationRequest) {
        walletService.processOperation(operationRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Перевод средств между кошельками", description = "Переводит указанную сумму с одного кошелька на другой.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    })
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @Parameter(description = "Данные перевода", required = true)
            @Valid @RequestBody TransferDTO transferDTO) {
        walletService.transfer(transferDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить список кошельков пользователя", description = "Возвращает список кошельков пользователя с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список кошельков получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content)
    })
    @GetMapping("/getUserWallets")
    public ResponseEntity<Page<WalletDto>> getWallets(
            @Parameter(description = "Номер страницы")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<WalletDto> wallets = walletService.getUserWallets(Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(wallets);
    }

    @Operation(summary = "Получить транзакции по кошельку", description = "Возвращает список транзакций указанного кошелька с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список транзакций получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден", content = @Content)
    })
    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @Parameter(description = "ID кошелька", required = true, in = ParameterIn.PATH)
            @PathVariable(name = "walletId") UUID walletId,
            @Parameter(description = "Номер страницы", required = false)
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", required = false)
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<TransactionDto> transactions = walletService.getTransactionsByWalletId(walletId, Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(transactions);
    }
}
