package com.sg.bank_account_api.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sg.bank_account_api.dto.AccountDto;
import com.sg.bank_account_api.dto.ClientDto;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Statement;
import com.sg.bank_account_api.model.Transaction;

@Component
public class DtoMapper {

    public Statement toStatement(Transaction transaction) {
        validate(transaction, Transaction.class);
        return new Statement(transaction.getDate(), transaction.getAmount(), transaction.getBalance());
    }

    public List<Statement> toStatementList(List<Transaction> transactions) {
        if (transactions == null) {
            return Collections.emptyList();
        }
        return transactions.stream()
                .map(this::toStatement)
                .collect(Collectors.toList());
    }

    public AccountDto accountToDto(Account account) {
        validate(account, Account.class);

        return new AccountDto(
                clientToDto(account.getClient()),
                account.getBalance(),
                account.getDate(),
                toStatementList(account.getTransactions()));
    }

    public Client dtoToClient(ClientDto dto) {
        validate(dto, ClientDto.class);
        return new Client(dto.lastname(), dto.firstname());
    }

    public ClientDto clientToDto(Client client) {
        validate(client, Client.class);
        return new ClientDto(client.getLastName(), client.getFirstName());
    }

    private void validate(Object obj, Class<?> type) {
        if (obj == null) {
            throw new IllegalArgumentException(String.format("%s can't be null", type.getSimpleName()));
        }
    }
}
