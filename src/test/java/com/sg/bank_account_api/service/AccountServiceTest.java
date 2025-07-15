package com.sg.bank_account_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sg.bank_account_api.dto.CreatedAccountDto;
import com.sg.bank_account_api.dto.TransactionDto;
import com.sg.bank_account_api.exceptions.AccountNotFoundException;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.exceptions.ClientNotFoundException;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Transaction;
import com.sg.bank_account_api.model.TransactionType;
import com.sg.bank_account_api.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_shouldReturnCreatedAccountDto() {
        Client client = new Client("idClient", "Doe", "John");
        Client createdClient = new Client("idCreated", "Doe", "John");
        Account savedAccount = new Account(createdClient);
        savedAccount.setId("acc123");

        when(clientService.createClient(client)).thenReturn(createdClient);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        CreatedAccountDto result = accountService.createAccount(client);

        assertEquals("acc123", result.accountId());
        assertEquals("idCreated", result.clientId());
    }

    @Test
    void getAccountById_shouldReturnAccount_whenFound() {
        Account account = new Account(new Client("id", "Doe", "John"));
        account.setId("acc123");
        when(accountRepository.findById("acc123")).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById("acc123");

        assertEquals("acc123", result.getId());
    }

    @Test
    void getAccountById_shouldThrowException_whenNotFound() {
        when(accountRepository.findById("notFound")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountById("notFound"));
    }

    @Test
    void performTransaction_shouldCreateDepositTransaction() {
        Account account = new Account(new Client("client123", "Doe", "John"));
        account.setId("acc123");
        account.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById("acc123")).thenReturn(Optional.of(account));

        TransactionDto dto = new TransactionDto("client123", "acc123", BigDecimal.valueOf(50));
        Transaction result = accountService.performTransaction(dto, TransactionType.DEPOSIT);

        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        assertEquals(BigDecimal.valueOf(50), result.getAmount());
        assertEquals(BigDecimal.valueOf(150), result.getBalance());
    }

    @Test
    void performTransaction_shouldThrowAmountException_whenInvalidAmount() {
        Account account = new Account(new Client("client123", "Doe", "John"));
        account.setId("acc123");
        account.setBalance(BigDecimal.valueOf(100));
        TransactionDto dto = new TransactionDto("client123", "acc123", BigDecimal.valueOf(150));

        when(accountRepository.findById("acc123")).thenReturn(Optional.of(account));

        assertThrows(AmountException.class, () -> accountService.performTransaction(dto, TransactionType.WITHDRAW));
    }

    @Test
    void performTransaction_shouldThrowClientNotFoundException_whenClientMismatch() {
        Account account = new Account(new Client("differentId", "Doe", "John"));
        account.setId("acc123");
        account.setBalance(BigDecimal.valueOf(100));
        TransactionDto dto = new TransactionDto("client123", "acc123", BigDecimal.valueOf(50));

        when(accountRepository.findById("acc123")).thenReturn(Optional.of(account));

        assertThrows(ClientNotFoundException.class,
                () -> accountService.performTransaction(dto, TransactionType.DEPOSIT));
    }

    @Test
    void updateAccount_shouldSaveAccount_whenNotNull() {
        Account account = new Account(new Client("client123", "Doe", "John"));
        account.setId("acc123");

        accountService.updateAcount(account);

        verify(accountRepository).save(account);
    }

    @Test
    void updateAccount_shouldDoNothing_whenNull() {
        accountService.updateAcount(null);

        verify(accountRepository, never()).save(any());
    }
}
