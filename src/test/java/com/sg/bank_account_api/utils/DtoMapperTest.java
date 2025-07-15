package com.sg.bank_account_api.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sg.bank_account_api.dto.AccountDto;
import com.sg.bank_account_api.dto.ClientDto;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Statement;
import com.sg.bank_account_api.model.Transaction;

@ExtendWith(MockitoExtension.class)
public class DtoMapperTest {

    private DtoMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new DtoMapper();
    }

    @Test
    void toStatement_shouldMapCorrectly() {
        Transaction transaction = new Transaction(BigDecimal.valueOf(100), BigDecimal.valueOf(500));
        Statement statement = mapper.toStatement(transaction);

        assertEquals(transaction.getDate(), statement.date());
        assertEquals(transaction.getAmount(), statement.amount());
        assertEquals(transaction.getBalance(), statement.balance());
    }

    @Test
    void toStatement_shouldThrowException_whenTransactionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.toStatement(null));
    }

    @Test
    void toStatementList_shouldReturnEmptyList_whenInputIsNull() {
        List<Statement> result = mapper.toStatementList(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void toStatementList_shouldMapAllTransactions() {
        List<Transaction> transactions = List.of(
                new Transaction(BigDecimal.valueOf(50), BigDecimal.valueOf(100)),
                new Transaction(LocalDate.now().minusDays(1), BigDecimal.valueOf(25), BigDecimal.valueOf(75)));

        List<Statement> result = mapper.toStatementList(transactions);

        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(50), result.get(0).amount());
    }

    @Test
    void accountToDto_shouldMapAllFieldsCorrectly() {
        Client client = new Client("Doe", "John");
        client.setId("cli123");
        Account account = new Account(client);
        account.setId("acc123");
        account.setBalance(BigDecimal.valueOf(500));
        account.setDate(LocalDate.now());
        account.setTransactions(List.of(
                new Transaction(LocalDate.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(600))));

        AccountDto dto = mapper.accountToDto(account);

        assertEquals("Doe", dto.client().lastname());
        assertEquals(BigDecimal.valueOf(500), dto.balance());
        assertFalse(dto.statements().isEmpty());
    }

    @Test
    void accountToDto_shouldThrowException_whenAccountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.accountToDto(null));
    }

    @Test
    void dtoToClient_shouldConvertCorrectly() {
        ClientDto dto = new ClientDto("Doe", "John");
        Client client = mapper.dtoToClient(dto);

        assertEquals("Doe", client.getLastName());
        assertEquals("John", client.getFirstName());
    }

    @Test
    void dtoToClient_shouldThrowException_whenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.dtoToClient(null));
    }

    @Test
    void clientToDto_shouldConvertCorrectly() {
        Client client = new Client("Doe", "John");
        ClientDto dto = mapper.clientToDto(client);

        assertEquals("Doe", dto.lastname());
        assertEquals("John", dto.firstname());
    }

    @Test
    void clientToDto_shouldThrowException_whenClientIsNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.clientToDto(null));
    }
}
