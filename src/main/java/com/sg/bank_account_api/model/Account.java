package com.sg.bank_account_api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Document(collection = "accounts")
public record Account(@Id String id, BigDecimal balance, Client client, LocalDate date, List<Statement> statements) {

}
