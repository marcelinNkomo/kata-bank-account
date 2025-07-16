package com.sg.bank_account_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementDto(LocalDateTime date, BigDecimal amount, BigDecimal balance) {

}
