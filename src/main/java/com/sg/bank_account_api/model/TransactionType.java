package com.sg.bank_account_api.model;

import com.sg.bank_account_api.exceptions.TransactionTypeException;

public enum TransactionType {
    DEPOSIT,
    WITHDRAW;

    static TransactionType of(String type) {
        try {
            return TransactionType.valueOf(type);
        } catch (Exception e) {
            throw new TransactionTypeException("TransactionType not found for type : " + type);
        }
    }
}
