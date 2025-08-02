package org.Akorad.wallets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.controller.WalletController;
import org.Akorad.dto.transaction.TransactionDto;
import org.Akorad.dto.wallet.OperationRequest;
import org.Akorad.dto.wallet.TransferDTO;
import org.Akorad.dto.wallet.WalletDto;
import org.Akorad.dto.wallet.WalletResponseDTO;
import org.Akorad.entity.OperationType;
import org.Akorad.security.JwtAuthenticationFilter;
import org.Akorad.security.JwtTokenProvider;
import org.Akorad.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем security фильтры для тестов
@Import(WalletControllerTest.MockConfig.class) // Импортируем мок-конфигурацию
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletService walletService;

    private UUID walletId = UUID.randomUUID();

    private WalletResponseDTO walletResponseDTO;

    @BeforeEach
    void setUp() {
        walletResponseDTO = new WalletResponseDTO();
        walletResponseDTO.setWalletId(walletId);
        walletResponseDTO.setBalance(BigDecimal.valueOf(100));
        walletResponseDTO.setOwnerName("Ivan Ivanov");
    }

    @Test
    void createWallet_ShouldReturnWalletResponseDTO() throws Exception {
        when(walletService.createWallet()).thenReturn(walletResponseDTO);

        mockMvc.perform(post("/api/v1/wallets/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(100))
                .andExpect(jsonPath("$.ownerName").value("Ivan Ivanov"));

        verify(walletService).createWallet();
    }

    @Test
    void deleteWallet_ShouldReturnNoContent() throws Exception {
        doNothing().when(walletService).deleteWallet(walletId);

        mockMvc.perform(delete("/api/v1/wallets/delete/{walletId}", walletId))
                .andExpect(status().isNoContent());

        verify(walletService).deleteWallet(walletId);
    }

    @Test
    void getBalance_ShouldReturnBalance() throws Exception {
        when(walletService.getBalance(walletId)).thenReturn(BigDecimal.valueOf(200));

        mockMvc.perform(get("/api/v1/wallets/getBalance/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().string("200"));

        verify(walletService).getBalance(walletId);
    }

    @Test
    void processOperation_ShouldReturnOk() throws Exception {
        OperationRequest request = new OperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(50));
        request.setComment("Test deposit");

        doNothing().when(walletService).processOperation(any(OperationRequest.class));

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        verify(walletService).processOperation(any(OperationRequest.class));
    }

    @Test
    void transfer_ShouldReturnOk() throws Exception {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromWalletId(walletId);
        transferDTO.setToWalletId(UUID.randomUUID());
        transferDTO.setAmount(BigDecimal.valueOf(100));
        transferDTO.setComment("Test transfer");

        doNothing().when(walletService).transfer(any(TransferDTO.class));

        mockMvc.perform(post("/api/v1/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(transferDTO)))
                .andExpect(status().isOk());

        verify(walletService).transfer(any(TransferDTO.class));
    }

    @Test
    void getWallets_ShouldReturnPagedWallets() throws Exception {
        WalletDto walletDto = new WalletDto();
        walletDto.setWalletId(walletId);
        walletDto.setBalance(BigDecimal.valueOf(123));

        Page<WalletDto> page = new PageImpl<>(List.of(walletDto));

        when(walletService.getUserWallets(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/wallets/getUserWallets")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.content[0].balance").value(123));

        verify(walletService).getUserWallets(any(Pageable.class));
    }

    @Test
    void getTransactions_ShouldReturnPagedTransactions() throws Exception {
        UUID walletId = UUID.randomUUID();

        TransactionDto transactionDto = new TransactionDto();
        // Заполнение полей по необходимости

        Page<TransactionDto> page = new PageImpl<>(List.of(transactionDto));

        when(walletService.getTransactionsByWalletId(eq(walletId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/wallets/{walletId}/transactions", walletId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(walletService).getTransactionsByWalletId(eq(walletId), any(Pageable.class));
    }

    // Вспомогательный метод для преобразования объекта в JSON строку
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Конфигурация с моками для необходимых бинов, чтобы не было проблем с загрузкой ApplicationContext
    @TestConfiguration
    static class MockConfig {

        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }

        @Bean
        public WalletService walletService() {
            return Mockito.mock(WalletService.class);
        }

    }
}
