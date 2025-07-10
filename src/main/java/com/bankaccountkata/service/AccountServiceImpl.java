package com.bankaccountkata.service;

import com.bankaccountkata.exception.DepositAmountException;
import com.bankaccountkata.exception.TransactionException;
import com.bankaccountkata.exception.WithdrawAmountException;
import com.bankaccountkata.model.Transaction;
import com.bankaccountkata.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final TransactionRepository repository;

    @Override
    public Transaction deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new DepositAmountException("Deposit must be positive");

        BigDecimal balance = getCurrentBalance().add(amount);
        Transaction tx = new Transaction(null, LocalDate.now(), amount, balance);
        return repository.save(tx);
    }

    @Override
    public Transaction withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new WithdrawAmountException("Withdrawal must be positive");

        BigDecimal balance = getCurrentBalance().subtract(amount);
        if (balance.compareTo(BigDecimal.ZERO) < 0)
            throw new TransactionException("Insufficient funds");

        Transaction tx = new Transaction(null, LocalDate.now(), amount.negate(), balance);
        return repository.save(tx);
    }

    @Override
    public List<Transaction> printStatement() {
        return repository.findAllByOrderByDateDesc();
    }

    private BigDecimal getCurrentBalance() {
        return repository.findAll().stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
