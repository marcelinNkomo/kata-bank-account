package com.sg.bank_account_api.exceptions;

/**
 * Classe qui permet de remonter les erreurs liées au client
 */
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String cause) {
        super(cause);
    }
}
