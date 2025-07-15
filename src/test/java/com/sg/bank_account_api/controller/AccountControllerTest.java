package com.sg.bank_account_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bank_account_api.controller.handler.ApiExceptionHandler;
import com.sg.bank_account_api.dto.ClientDto;
import com.sg.bank_account_api.dto.TransactionDto;
import com.sg.bank_account_api.exceptions.AccountNotFoundException;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.model.TransactionType;
import com.sg.bank_account_api.service.AccountService;
import com.sg.bank_account_api.utils.DtoMapper;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private DtoMapper mapper;

    @InjectMocks
    private AccountController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
    }

    @Test
    void create_shouldReturn400_whenClientDtoIsNull() throws Exception {
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")) // contenu vide
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void create_shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
        ClientDto dto = new ClientDto("Doe", "John");
        when(mapper.dtoToClient(any())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Unexpected error"))
            .andExpect(jsonPath("$.statusCode").value(500));
    }

    @Test
    void deposit_shouldReturn404_whenAccountNotFound() throws Exception {
        TransactionDto dto = new TransactionDto("cli42", "missingAcc", BigDecimal.valueOf(100));
        when(accountService.performTransaction(dto, TransactionType.DEPOSIT))
            .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(post("/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Account not found"))
            .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void withdraw_shouldReturn400_whenAmountInvalid() throws Exception {
        TransactionDto dto = new TransactionDto("cli42", "acc42", BigDecimal.valueOf(-5));
        when(accountService.performTransaction(dto, TransactionType.WITHDRAW))
            .thenThrow(new AmountException("Invalid amount"));

        mockMvc.perform(post("/account/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid amount"))
            .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void printStatement_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.getAccountById("unknownId"))
            .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get("/account/statement/unknownId"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Account not found"))
            .andExpect(jsonPath("$.statusCode").value(404));
    }
}
