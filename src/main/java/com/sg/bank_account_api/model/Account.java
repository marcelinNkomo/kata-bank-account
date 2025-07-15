package com.sg.bank_account_api.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {
    @Id
    private String id;
    private BigDecimal balance;
    @DBRef
    private Client client;
    private LocalDate date;
    private List<Transaction> transactions = new ArrayList<>();

    public Account(Client client) {
        this.client = client;
        this.balance = BigDecimal.ZERO;
        this.date = LocalDate.now();
    }
}
