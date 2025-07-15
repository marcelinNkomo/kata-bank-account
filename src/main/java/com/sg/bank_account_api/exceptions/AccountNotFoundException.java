package com.sg.bank_account_api.exceptions;

/**
 * Classe qui permet de remonter les erreurs liées au compte
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String cause) {
        super(cause);
    }
}
