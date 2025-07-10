package com.bankaccountkata.exception;

public class TransactionException extends RuntimeException {
    public TransactionException(String cause) {
        super(cause);
    }
}
