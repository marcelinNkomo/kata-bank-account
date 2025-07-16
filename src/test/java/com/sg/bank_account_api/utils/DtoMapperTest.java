package com.sg.bank_account_api.utils;

import com.sg.bank_account_api.dto.AccountDto;
import com.sg.bank_account_api.dto.ClientDto;
import com.sg.bank_account_api.dto.StatementDto;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DtoMapperTest {
    private DtoMapper dtoMapper;

    @BeforeEach
    void setUp() {
        dtoMapper = new DtoMapper();
    }

    @Test
    @DisplayName("Should map Account to AccountDto successfully")
    void shouldMapAccountToAccountDtoSuccessfully() {
        Account account = getAccount();

        AccountDto accountDto = dtoMapper.accountToDto(account);

        assertThat(accountDto).isNotNull();
        assertThat(accountDto.balance()).isEqualTo(BigDecimal.valueOf(130));
        assertThat(accountDto.date()).isEqualTo(LocalDate.of(2023, 1, 5));

        assertThat(accountDto.client()).isNotNull();
        assertThat(accountDto.client().id()).isEqualTo("client123");
        assertThat(accountDto.client().lastName()).isEqualTo("Doe");
        assertThat(accountDto.client().firstName()).isEqualTo("John");

        assertThat(accountDto.statements()).hasSize(2);
        assertThat(accountDto.statements().get(0).amount()).isEqualTo(BigDecimal.valueOf(50));
        assertThat(accountDto.statements().get(1).balance()).isEqualTo(BigDecimal.valueOf(130));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when mapping null Account to AccountDto")
    void shouldThrowIllegalArgumentExceptionWhenMappingNullAccountToAccountDto() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                dtoMapper.accountToDto(null));

        assertThat(thrown.getMessage()).contains("Account can't be null");
    }

    @Test
    @DisplayName("Should map Statement to StatementDto successfully")
    void shouldMapStatementToStatementDtoSuccessfully() {
        Statement statement = new Statement(LocalDate.of(2024, 2, 1), BigDecimal.valueOf(200), BigDecimal.valueOf(300));

        StatementDto statementDto = dtoMapper.statementToDto(statement);

        assertThat(statementDto).isNotNull();
        assertThat(statementDto.date()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(statementDto.amount()).isEqualTo(BigDecimal.valueOf(200));
        assertThat(statementDto.balance()).isEqualTo(BigDecimal.valueOf(300));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when mapping null Statement to StatementDto")
    void shouldThrowIllegalArgumentExceptionWhenMappingNullStatementToStatementDto() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                dtoMapper.statementToDto(null));

        assertThat(thrown.getMessage()).contains("Statement can't be null");
    }

    @Test
    @DisplayName("Should map List of Statement to List of StatementDto successfully")
    void shouldMapListOfStatementToListOfStatementDtoSuccessfully() {
        Statement statement1 = new Statement(LocalDate.of(2024, 3, 1), BigDecimal.valueOf(100), BigDecimal.valueOf(100));
        Statement statement2 = new Statement(LocalDate.of(2024, 3, 5), BigDecimal.valueOf(-30), BigDecimal.valueOf(70));
        List<Statement> statements = Arrays.asList(statement1, statement2);

        List<StatementDto> statementDtos = dtoMapper.toStatementDtoList(statements);

        assertThat(statementDtos).isNotNull();
        assertThat(statementDtos).hasSize(2);
        assertThat(statementDtos.get(0).amount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(statementDtos.get(1).balance()).isEqualTo(BigDecimal.valueOf(70));
    }

    @Test
    @DisplayName("Should return empty list when mapping null List of Statement")
    void shouldReturnEmptyListWhenMappingNullListOfStatement() {
        List<StatementDto> statementDtos = dtoMapper.toStatementDtoList(null);

        assertThat(statementDtos).isNotNull();
        assertThat(statementDtos).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when mapping empty List of Statement")
    void shouldReturnEmptyListWhenMappingEmptyListOfStatement() {
        List<StatementDto> statementDtos = dtoMapper.toStatementDtoList(Collections.emptyList());

        assertThat(statementDtos).isNotNull();
        assertThat(statementDtos).isEmpty();
    }

    @Test
    @DisplayName("Should map Client to ClientDto successfully")
    void shouldMapClientToClientDtoSuccessfully() {
        Client client = new Client("client789", "Smith", "Jane", LocalDate.of(2022, 12, 1));

        ClientDto clientDto = dtoMapper.clientToDto(client);

        assertThat(clientDto).isNotNull();
        assertThat(clientDto.id()).isEqualTo("client789");
        assertThat(clientDto.lastName()).isEqualTo("Smith");
        assertThat(clientDto.firstName()).isEqualTo("Jane");
        assertThat(clientDto.date()).isEqualTo(LocalDate.of(2022, 12, 1));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when mapping null Client to ClientDto")
    void shouldThrowIllegalArgumentExceptionWhenMappingNullClientToClientDto() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                dtoMapper.clientToDto(null));

        assertThat(thrown.getMessage()).contains("Client can't be null");
    }

    private static Account getAccount() {
        Client client = new Client("client123", "Doe", "John", LocalDate.of(2023, 1, 1));
        Statement statement1 = new Statement(LocalDate.of(2024, 1, 10), BigDecimal.valueOf(50), BigDecimal.valueOf(150));
        Statement statement2 = new Statement(LocalDate.of(2024, 1, 15), BigDecimal.valueOf(-20), BigDecimal.valueOf(130));
        List<Statement> statements = Arrays.asList(statement1, statement2);
        return new Account("account456", BigDecimal.valueOf(130), client, LocalDate.of(2023, 1, 5), statements);
    }
}