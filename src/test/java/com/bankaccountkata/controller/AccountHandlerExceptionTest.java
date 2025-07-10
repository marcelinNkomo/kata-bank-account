package com.bankaccountkata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bankaccountkata.exception.DepositAmountException;
import com.bankaccountkata.exception.ErrorResponse;

public class AccountHandlerExceptionTest {

    private final AccountHandlerException handler = new AccountHandlerException();

    @Test
    void handleAccountException_shouldReturnBadRequest() {
        Exception ex = new DepositAmountException("Invalid deposit");

        ResponseEntity<ErrorResponse> response = handler.handleAccountException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatusCode());
        assertEquals("Invalid deposit", body.getMessage());
    }

    @Test
    void handleGlobalException_shouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getStatusCode());
        assertEquals("Unexpected error", body.getMessage());
    }
}
