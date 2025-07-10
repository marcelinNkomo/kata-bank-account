package com.bankaccountkata.service;

import com.bankaccountkata.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Transaction deposit(BigDecimal amount);
    Transaction withdraw(BigDecimal amount);
    List<Transaction> printStatement();
}
