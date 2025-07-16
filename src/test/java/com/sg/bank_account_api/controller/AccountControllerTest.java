package com.sg.bank_account_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.bank_account_api.controller.handler.ApiExceptionHandler;
import com.sg.bank_account_api.dto.AccountDto;
import com.sg.bank_account_api.dto.ClientDto;
import com.sg.bank_account_api.dto.CreateClientDto;
import com.sg.bank_account_api.dto.CreateTransactionDto;
import com.sg.bank_account_api.dto.CreatedAccountDto;
import com.sg.bank_account_api.dto.StatementDto;
import com.sg.bank_account_api.exceptions.AccountNotFoundException;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.exceptions.ClientNotFoundException;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.TransactionType;
import com.sg.bank_account_api.service.AccountService;
import com.sg.bank_account_api.service.IAccountService;
import com.sg.bank_account_api.utils.DtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private final IAccountService accountService = Mockito.mock(AccountService.class);
    private final DtoMapper dtoMapper = Mockito.mock(DtoMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private CreateClientDto createClientDto;
    private CreatedAccountDto createdAccountDto;
    private CreateTransactionDto createTransactionDto;
    private StatementDto statementDto;
    private Account testAccount;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        AccountController accountController = new AccountController(accountService, dtoMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();

        createClientDto = new CreateClientDto("Doe", "John");
        createdAccountDto = new CreatedAccountDto("account123", "client456");
        createTransactionDto = new CreateTransactionDto("client456", "account123", BigDecimal.valueOf(100));
        statementDto = new StatementDto(LocalDate.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(100));

        Client testClient = new Client("client456", "Doe", "John", LocalDate.now());
        testAccount = new Account("account123", BigDecimal.valueOf(100), testClient, LocalDate.now(), new ArrayList<>());
        accountDto = new AccountDto(new ClientDto("client456", "Doe", "John", LocalDate.now()), BigDecimal.valueOf(100), LocalDate.now(), Collections.emptyList());
    }

    @Test
    @DisplayName("Should create account and return CREATED status")
    void shouldCreateAccountAndReturnCreated() throws Exception {
        when(accountService.createAccount(any(CreateClientDto.class))).thenReturn(createdAccountDto);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value("account123"))
                .andExpect(jsonPath("$.clientId").value("client456"));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException when creating account with invalid DTO")
    void shouldHandleIllegalArgumentExceptionWhenCreateAccountWithInvalidDto() throws Exception {
        CreateClientDto invalidDto = new CreateClientDto("", ""); // Invalid DTO
        when(accountService.createAccount(any(CreateClientDto.class)))
                .thenThrow(new IllegalArgumentException("Client can't be null and should have either lastname or fisrtname"));

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Client can't be null and should have either lastname or fisrtname"));
    }

    @Test
    @DisplayName("Should perform deposit and return CREATED status")
    void shouldPerformDepositAndReturnCreated() throws Exception {
        when(accountService.performTransaction(any(CreateTransactionDto.class), eq(TransactionType.DEPOSIT)))
                .thenReturn(statementDto);

        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    @DisplayName("Should handle AccountNotFoundException for deposit")
    void shouldHandleAccountNotFoundExceptionForDeposit() throws Exception {
        when(accountService.performTransaction(any(CreateTransactionDto.class), eq(TransactionType.DEPOSIT)))
                .thenThrow(new AccountNotFoundException("Account not found for ID : account123"));


        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Account not found for ID : account123"));
    }

    @Test
    @DisplayName("Should handle AmountException for deposit")
    void shouldHandleAmountExceptionForDeposit() throws Exception {
        when(accountService.getAccountById(anyString())).thenReturn(testAccount);

        when(accountService.performTransaction(any(CreateTransactionDto.class), eq(TransactionType.DEPOSIT)))
                .thenThrow(new AmountException("Amount must be > 0"));

        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be > 0"));
    }

    @Test
    @DisplayName("Should handle ClientNotFoundException for deposit")
    void shouldHandleClientNotFoundExceptionForDeposit() throws Exception {
        Client mismatchedClient = new Client("mismatchedClient", "Another", "Person", LocalDate.now());
        Account accountWithMismatchedClient = new Account("account123", BigDecimal.valueOf(100), mismatchedClient, LocalDate.now(), new ArrayList<>());
        when(accountService.getAccountById(anyString())).thenReturn(accountWithMismatchedClient);


        when(accountService.performTransaction(any(CreateTransactionDto.class), eq(TransactionType.DEPOSIT)))
                .thenThrow(new ClientNotFoundException("Client with id " + createTransactionDto.clientId() + " is not associated with account " + createTransactionDto.accountId()));

        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Client with id " + createTransactionDto.clientId() + " is not associated with account " + createTransactionDto.accountId()));
    }


    @Test
    @DisplayName("Should perform withdrawal and return CREATED status")
    void shouldPerformWithdrawalAndReturnCreated() throws Exception {
        when(accountService.performTransaction(any(CreateTransactionDto.class), eq(TransactionType.WITHDRAW)))
                .thenReturn(statementDto);

        mockMvc.perform(post("/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    @DisplayName("Should handle AmountException for withdrawal")
    void shouldHandleAmountExceptionForWithdrawal() throws Exception {
        when(accountService.getAccountById(anyString())).thenReturn(testAccount);

        when(accountService.performTransaction(any(CreateTransactionDto.class), eq(TransactionType.WITHDRAW)))
                .thenThrow(new AmountException("Amount must be <= balance in the case of a withdrawal"));

        mockMvc.perform(post("/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be <= balance in the case of a withdrawal"));
    }

    @Test
    @DisplayName("Should print statement and return OK status")
    void shouldPrintStatementAndReturnOk() throws Exception {
        when(accountService.getAccountById("account123")).thenReturn(testAccount);
        when(dtoMapper.accountToDto(any(Account.class))).thenReturn(accountDto);

        mockMvc.perform(get("/account/statement/{accountId}", "account123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client.id").value("client456"))
                .andExpect(jsonPath("$.balance").value(100.0));
    }

    @Test
    @DisplayName("Should handle AccountNotFoundException for print statement")
    void shouldHandleAccountNotFoundExceptionForPrintStatement() throws Exception {
        when(accountService.getAccountById("nonExistentAccount")).thenThrow(new AccountNotFoundException("Account not found for ID : nonExistentAccount"));

        mockMvc.perform(get("/account/statement/{accountId}", "nonExistentAccount"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Account not found for ID : nonExistentAccount"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException for invalid request body")
    void shouldHandleHttpMessageNotReadableException() throws Exception {
        String invalidJson = "{\"clientId\": \"abc\", \"accountId\": \"xyz\", \"amount\": \"invalid_amount\"}";

        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    @DisplayName("Should handle generic Exception for unexpected errors")
    void shouldHandleGenericException() throws Exception {
        when(accountService.createAccount(any(CreateClientDto.class)))
                .thenThrow(new RuntimeException("Something unexpected happened!"));

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.message").value("Something unexpected happened!"));
    }

}