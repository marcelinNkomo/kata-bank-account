package com.sg.bank_account_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AccountDto(ClientDto client, BigDecimal balance, LocalDateTime date, List<StatementDto> statements) {

}
