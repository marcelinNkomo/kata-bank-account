package com.sg.bank_account_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.sg.bank_account_api.model.Statement;


public record AccountDto(ClientDto client, BigDecimal balance, LocalDate date, List<Statement> statements) {

}
