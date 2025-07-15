package com.sg.bank_account_api.dto;

/**
 * Classe qui va contenir les élements de réponse liés aux erreurs 
 */
public record ErrorResponse(int statusCode, String message) {

}
