package com.bankaccountkata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bankaccountkata.exception.DepositAmountException;
import com.bankaccountkata.exception.TransactionException;
import com.bankaccountkata.exception.WithdrawAmountException;
import com.bankaccountkata.model.Transaction;
import com.bankaccountkata.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private TransactionRepository repository;

    private AccountService accountService;

    @BeforeEach
    void setup() {
        accountService = new AccountServiceImpl(repository);
    }

    /**
     * Deposit assertions
     */
    @Test
    void depositShouldRaiseException_whenGivenAmountIsZero() {

        Exception ex = assertThrows(DepositAmountException.class,
                () -> accountService.deposit(BigDecimal.ZERO));

        assertThat("Deposit must be positive").isEqualTo(ex.getMessage());
    }

    @Test
    void depositShouldRaiseException_whenGivenAmountIsNegative() {

        Exception ex = assertThrows(DepositAmountException.class,
                () -> accountService.deposit(BigDecimal.valueOf(-10)));

        assertThat("Deposit must be positive").isEqualTo(ex.getMessage());
    }

    @Test
    void depositShouldReturnTransaction_whenGivenAmountIsPositive_andThereIsAnExistingTransaction() {
        // Given
        when(repository.findAll()).thenReturn(Collections.singletonList(
                new Transaction("tx1", LocalDate.now(), new BigDecimal(100), new BigDecimal(100))));
        when(repository.save(any(Transaction.class))).thenReturn(null);

        // When
        accountService.deposit(new BigDecimal(110));

        // Then
        verify(repository).save(new Transaction(null, LocalDate.now(), new BigDecimal(110), new BigDecimal(210)));
    }

    @Test
    void depositShouldReturnTransaction_whenGivenAmountIsPositive_andThereIsNoExistingTransaction() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(Transaction.class))).thenReturn(null);

        // When
        accountService.deposit(new BigDecimal(110));

        // Then
        verify(repository).save(new Transaction(null, LocalDate.now(), new BigDecimal(110), new BigDecimal(110)));
    }

    /**
     * Withdraw assertions
     */

    @Test
    void withdrawShouldRaiseException_whenGivenAmountIsZero() {

        Exception ex = assertThrows(WithdrawAmountException.class,
                () -> accountService.withdraw(BigDecimal.ZERO));

        assertEquals("Withdrawal must be positive", ex.getMessage());
    }

    @Test
    void withdrawShouldRaiseException_whenGivenAmountIsNegative() {

        Exception ex = assertThrows(WithdrawAmountException.class,
                () -> accountService.withdraw(BigDecimal.valueOf(-10)));

        assertEquals("Withdrawal must be positive", ex.getMessage());
    }

    @Test
    void withdrawShouldRaiseException_whenBalanceIsLessThanGivenAmount() {

        when(repository.findAll()).thenReturn(Collections.singletonList(
                new Transaction("tx2", LocalDate.now(), new BigDecimal(100), new BigDecimal(100))));

        // When

        Exception ex = assertThrows(TransactionException.class,
                () -> accountService.withdraw(new BigDecimal(300)));

        assertEquals("Insufficient funds", ex.getMessage());
    }

    @Test
    void withdrawShouldReturnTransaction_whenGivenAmountIsPositiveAndLessThanBalance() {
        // Given
        when(repository.findAll()).thenReturn(Collections.singletonList(
                new Transaction("tx3", LocalDate.now(), new BigDecimal(500), new BigDecimal(500))));
        when(repository.save(any(Transaction.class))).thenReturn(null);

        // When
        accountService.withdraw(new BigDecimal(110));

        // Then
        verify(repository).save(new Transaction(null, LocalDate.now(), new BigDecimal(-110), new BigDecimal(390)));
    }

    /**
     * Statement assertions
     */
    @Test
    void printStatementShouldRetrunEmptyList() {
        // Given
        when(repository.findAllByOrderByDateDesc()).thenReturn(Collections.emptyList());

        // When
        List<Transaction> transactions = accountService.printStatement();

        // Then
        assertThat(transactions).isEmpty();
    }

    @Test
    void printStatementShouldRetrunTranctionsList() {
        // Given
        when(repository.findAllByOrderByDateDesc()).thenReturn(
                Arrays.asList(
                        new Transaction("1", LocalDate.now().minusDays(2), new BigDecimal(400), new BigDecimal(400)),
                        new Transaction("2", LocalDate.now(), new BigDecimal(-50), new BigDecimal(350))));

        // When
        List<Transaction> transactions = accountService.printStatement();

        // Then
        assertThat(transactions).hasSize(2);
    }
}