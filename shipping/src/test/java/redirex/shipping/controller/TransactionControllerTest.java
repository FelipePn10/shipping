package redirex.shipping.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import redirex.shipping.dto.request.DepositRequestDto;
import redirex.shipping.dto.response.WalletTransactionResponse;
import redirex.shipping.service.UserWalletServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserWalletServiceImpl userWalletService;

    @Test
    void depositToWallet_Success() throws Exception {
        DepositRequestDto request = new DepositRequestDto(new BigDecimal("100.00"), "pm_test");

        WalletTransactionResponse mockResponse = WalletTransactionResponse.builder()
                .status("success")
                .userId(1L)
                .amount(new BigDecimal("95.00"))
                .fee("5.00")
                .currency("CNY")
                .chargedAmount("125.00")
                .transactionDescription("Depósito simulado")
                .build();

        when(userWalletService.depositToWallet(anyLong(), any(DepositRequestDto.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/private/v1/api/users/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.amount").value(95.00))
                .andExpect(jsonPath("$.chargedAmount").value("125.00"));
    }

    @Test
    void depositToWallet_InvalidRequest() throws Exception {
        // Valor inválido (<= 50)
        DepositRequestDto invalidRequest = new DepositRequestDto(new BigDecimal("10.00"), "pm_test");

        mockMvc.perform(post("/private/v1/api/users/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}