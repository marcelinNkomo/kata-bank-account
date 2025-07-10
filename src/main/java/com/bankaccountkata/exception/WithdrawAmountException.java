package com.bankaccountkata.exception;

public class WithdrawAmountException extends RuntimeException {
    public WithdrawAmountException(String cause) {
        super(cause);
    }
}
