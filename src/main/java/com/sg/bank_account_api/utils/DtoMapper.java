package com.sg.bank_account_api.utils;

import com.sg.bank_account_api.dto.AccountDto;
import com.sg.bank_account_api.dto.ClientDto;
import com.sg.bank_account_api.dto.StatementDto;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Statement;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public AccountDto accountToDto(Account account) {
        validate(account, Account.class);
        return new AccountDto(
                clientToDto(account.client()),
                account.balance(),
                account.date(),
                toStatementDtoList(account.statements()));
    }

    public StatementDto statementToDto(Statement statement) {
        validate(statement, Statement.class);
        return new StatementDto(statement.date(), statement.amount(), statement.balance());
    }

    public List<StatementDto> toStatementDtoList(List<Statement> statements) {
        if (statements == null) {
            return Collections.emptyList();
        }
        return statements.stream().map(this::statementToDto).collect(Collectors.toList());
    }

    public ClientDto clientToDto(Client client) {
        validate(client, Client.class);
        return new ClientDto(client.id(), client.lastname(), client.firstname(), client.date());
    }

    private void validate(Object obj, Class<?> type) {
        if (obj == null) {
            throw new IllegalArgumentException(String.format("%s can't be null", type.getSimpleName()));
        }
    }
}
