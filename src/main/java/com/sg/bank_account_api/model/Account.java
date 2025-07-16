package com.sg.bank_account_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "accounts")
public record Account(@Id String id, BigDecimal balance, Client client, LocalDateTime date, List<Statement> statements) {

}
