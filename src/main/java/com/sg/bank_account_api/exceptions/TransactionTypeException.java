package com.sg.bank_account_api.exceptions;

/**
 * Classe qui permet de remonter les erreurs liées aux types de transaction
 */
public class TransactionTypeException extends RuntimeException {
    public TransactionTypeException(String cause) {
        super(cause);
    }
}
