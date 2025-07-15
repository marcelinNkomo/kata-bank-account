package com.sg.bank_account_api.exceptions;

/**
 * Classe qui permet de remonter les erreurs liées aux montants
 */
public class AmountException extends RuntimeException {
    public AmountException(String cause) {
        super(cause);
    }
}
