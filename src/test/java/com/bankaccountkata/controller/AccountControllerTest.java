package com.bankaccountkata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bankaccountkata.model.Transaction;
import com.bankaccountkata.service.AccountServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountServiceImpl accountService;

    @InjectMocks
    private AccountController accountController;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction(
                "tx123",
                LocalDate.now(),
                new BigDecimal("100.00"),
                new BigDecimal("1000.00"));
    }

    @Test
    void deposit_shouldReturnCreatedTransaction() {
        BigDecimal amount = new BigDecimal("100.00");
        when(accountService.deposit(amount)).thenReturn(transaction);

        ResponseEntity<Transaction> response = accountController.deposit(amount);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void withdraw_shouldReturnCreatedTransaction() {
        BigDecimal amount = new BigDecimal("50.00");
        when(accountService.withdraw(amount)).thenReturn(transaction);

        ResponseEntity<Transaction> response = accountController.withdraw(amount);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void printStatement_shouldReturnListOfTransactions() {
        List<Transaction> transactions = List.of(transaction);
        when(accountService.printStatement()).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = accountController.printStatement();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }
}
