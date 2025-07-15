package com.sg.bank_account_api.controller.handler;

import com.sg.bank_account_api.dto.ErrorResponse;
import com.sg.bank_account_api.exceptions.AccountNotFoundException;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.exceptions.ClientNotFoundException;
import com.sg.bank_account_api.exceptions.TransactionTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Classe qui gère les exceptions levées
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(exception = {
            AccountNotFoundException.class,
            ClientNotFoundException.class})
    public ResponseEntity<ErrorResponse> notFoundException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = {
            TransactionTypeException.class,
            AmountException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> transactionTypeOrBadAmountException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(exception = {Exception.class})
    public ResponseEntity<ErrorResponse> anotherException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
