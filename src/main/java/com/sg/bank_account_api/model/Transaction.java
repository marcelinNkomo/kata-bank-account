package com.sg.bank_account_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDate date;

    public Transaction(BigDecimal amount, BigDecimal balance) {
        this.amount = amount;
        this.balance = balance;
        this.date = LocalDate.now();
    }

    public Transaction(LocalDate date, BigDecimal amount, BigDecimal balance) {
        this.amount = amount;
        this.balance = balance;
        this.date = date;
    }
}
