package com.sg.bank_account_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AccountDto(ClientDto client, BigDecimal balance, LocalDate date, List<StatementDto> statements) {

}
