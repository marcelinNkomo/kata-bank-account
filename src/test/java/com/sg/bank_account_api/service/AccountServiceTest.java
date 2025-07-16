package com.sg.bank_account_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.sg.bank_account_api.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AccountService accountService;

    private Client testClient;
    private Account testAccount;
    private CreateClientDto createClientDto;

    @BeforeEach
    void setUp() {
        testClient = new Client("client123", "Doe", "John", LocalDateTime.now());
        testAccount = new Account("account456", BigDecimal.ZERO, testClient, LocalDateTime.now(), new ArrayList<>());
        createClientDto = new CreateClientDto("Doe", "John");
    }

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {
        when(clientService.createClient(any(CreateClientDto.class))).thenReturn(testClient);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        CreatedAccountDto result = accountService.createAccount(createClientDto);

        assertThat(result).isNotNull();
        assertThat(result.accountId()).isEqualTo("account456");
        assertThat(result.clientId()).isEqualTo("client123");
        verify(clientService, times(1)).createClient(createClientDto);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should perform deposit successfully")
    void shouldPerformDepositSuccessfully() {
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        BigDecimal depositAmount = BigDecimal.valueOf(50);
        Account accountWithBalance = new Account("account456", initialBalance, testClient, LocalDateTime.now(),
                new ArrayList<>());
        CreateTransactionDto depositDto = new CreateTransactionDto("client123", "account456", depositAmount);

        when(accountRepository.findById("account456")).thenReturn(Optional.of(accountWithBalance));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);
            assertThat(savedAccount.balance()).isEqualTo(initialBalance.add(depositAmount));
            assertThat(savedAccount.statements()).hasSize(1);
            return savedAccount;
        });

        StatementDto result = accountService.performTransaction(depositDto, TransactionType.DEPOSIT);

        assertThat(result).isNotNull();
        assertThat(result.amount()).isEqualTo(depositAmount);
        assertThat(result.balance()).isEqualTo(initialBalance.add(depositAmount));
        assertThat(result.date().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        verify(accountRepository, times(1)).findById("account456");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should perform withdrawal successfully")
    void shouldPerformWithdrawalSuccessfully() {
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        BigDecimal withdrawalAmount = BigDecimal.valueOf(50);
        Account accountWithBalance = new Account("account456", initialBalance, testClient, LocalDateTime.now(),
                new ArrayList<>());
        CreateTransactionDto withdrawalDto = new CreateTransactionDto("client123", "account456", withdrawalAmount);

        when(accountRepository.findById("account456")).thenReturn(Optional.of(accountWithBalance));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);
            assertThat(savedAccount.balance()).isEqualTo(initialBalance.subtract(withdrawalAmount));
            assertThat(savedAccount.statements()).hasSize(1);
            return savedAccount;
        });

        StatementDto result = accountService.performTransaction(withdrawalDto, TransactionType.WITHDRAW);

        assertThat(result).isNotNull();
        assertThat(result.amount()).isEqualTo(withdrawalAmount.negate());
        assertThat(result.balance()).isEqualTo(initialBalance.subtract(withdrawalAmount));
        assertThat(result.date().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        verify(accountRepository, times(1)).findById("account456");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when performing transaction on non-existent account")
    void shouldThrowAccountNotFoundExceptionWhenTransactionOnNonExistentAccount() {
        CreateTransactionDto dto = new CreateTransactionDto("client123", "nonExistentAccount", BigDecimal.TEN);
        when(accountRepository.findById("nonExistentAccount")).thenReturn(Optional.empty());

        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class,
                () -> accountService.performTransaction(dto, TransactionType.DEPOSIT));

        assertThat(thrown.getMessage()).contains("Account not found for ID : nonExistentAccount");
        verify(accountRepository, times(1)).findById("nonExistentAccount");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw AmountException for negative deposit amount")
    void shouldThrowAmountExceptionForNegativeDeposit() {
        CreateTransactionDto depositDto = new CreateTransactionDto("client123", "account456", BigDecimal.valueOf(-10));
        when(accountRepository.findById("account456")).thenReturn(Optional.of(testAccount));

        AmountException thrown = assertThrows(AmountException.class,
                () -> accountService.performTransaction(depositDto, TransactionType.DEPOSIT));

        assertThat(thrown.getMessage()).contains("Amount must be > 0");
        verify(accountRepository, times(1)).findById("account456");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw AmountException for zero deposit amount")
    void shouldThrowAmountExceptionForZeroDeposit() {
        CreateTransactionDto depositDto = new CreateTransactionDto("client123", "account456", BigDecimal.ZERO);
        when(accountRepository.findById("account456")).thenReturn(Optional.of(testAccount));

        AmountException thrown = assertThrows(AmountException.class,
                () -> accountService.performTransaction(depositDto, TransactionType.DEPOSIT));

        assertThat(thrown.getMessage()).contains("Amount must be > 0");
        verify(accountRepository, times(1)).findById("account456");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw AmountException for withdrawal exceeding balance")
    void shouldThrowAmountExceptionForWithdrawalExceedingBalance() {
        BigDecimal initialBalance = BigDecimal.valueOf(50);
        BigDecimal withdrawalAmount = BigDecimal.valueOf(100);
        Account accountWithBalance = new Account("account456", initialBalance, testClient, LocalDateTime.now(),
                new ArrayList<>());
        CreateTransactionDto withdrawalDto = new CreateTransactionDto("client123", "account456", withdrawalAmount);

        when(accountRepository.findById("account456")).thenReturn(Optional.of(accountWithBalance));

        AmountException thrown = assertThrows(AmountException.class,
                () -> accountService.performTransaction(withdrawalDto, TransactionType.WITHDRAW));

        assertThat(thrown.getMessage())
                .contains("Amount must be > 0 and must be <= balance in the case of a withdrawal");
        verify(accountRepository, times(1)).findById("account456");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw ClientNotFoundException when client ID does not match account's client")
    void shouldThrowClientNotFoundExceptionWhenClientDoesNotMatchAccount() {
        Client anotherClient = new Client("anotherClient", "Smith", "Jane", LocalDateTime.now());
        Account accountWithAnotherClient = new Account("account456", BigDecimal.TEN, anotherClient, LocalDateTime.now(),
                new ArrayList<>());
        CreateTransactionDto dto = new CreateTransactionDto("client123", "account456", BigDecimal.TEN);

        when(accountRepository.findById("account456")).thenReturn(Optional.of(accountWithAnotherClient));

        ClientNotFoundException thrown = assertThrows(ClientNotFoundException.class,
                () -> accountService.performTransaction(dto, TransactionType.DEPOSIT));

        assertThat(thrown.getMessage()).contains("Client with id client123 is not associated with account account456");
        verify(accountRepository, times(1)).findById("account456");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should retrieve account by ID successfully")
    void shouldGetAccountByIdSuccessfully() {
        when(accountRepository.findById("account456")).thenReturn(Optional.of(testAccount));

        Account foundAccount = accountService.getAccountById("account456");

        assertThat(foundAccount).isEqualTo(testAccount);
        verify(accountRepository, times(1)).findById("account456");
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account by ID not found")
    void shouldThrowAccountNotFoundExceptionWhenGetAccountByIdNotFound() {
        when(accountRepository.findById("nonExistentAccount")).thenReturn(Optional.empty());

        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountById("nonExistentAccount"));

        assertThat(thrown.getMessage()).contains("Account not found for ID : nonExistentAccount");
        verify(accountRepository, times(1)).findById("nonExistentAccount");
    }

    @Test
    @DisplayName("Should update account successfully")
    void shouldUpdateAccountSuccessfully() {
        Account updatedAccount = new Account("account456", BigDecimal.valueOf(200), testClient, LocalDateTime.now(),
                new ArrayList<>());
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        Account result = accountService.updateAcount(updatedAccount);

        assertThat(result).isEqualTo(updatedAccount);
        verify(accountRepository, times(1)).save(updatedAccount);
    }
}