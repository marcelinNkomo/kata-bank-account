package com.bankaccountkata.controller;

import com.bankaccountkata.model.Transaction;
import com.bankaccountkata.service.AccountService;
import com.bankaccountkata.service.AccountServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@RequestParam BigDecimal amount) {
        Transaction tx = service.deposit(amount);
        return new ResponseEntity<>(tx, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestParam BigDecimal amount) {
        Transaction tx = service.withdraw(amount);
        return new ResponseEntity<>(tx, HttpStatus.CREATED);
    }

    @GetMapping("/statement")
    public ResponseEntity<List<Transaction>> printStatement() {
        List<Transaction> transactions = service.printStatement();
        return ResponseEntity.ok(transactions);
    }
}
