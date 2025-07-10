package com.bankaccountkata.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal balance;

    public Transaction(String id, LocalDate date, BigDecimal amount, BigDecimal balance) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.balance = balance;
    }
}
