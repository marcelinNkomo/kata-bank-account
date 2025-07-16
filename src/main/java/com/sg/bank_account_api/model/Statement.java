package com.sg.bank_account_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Statement(LocalDateTime date, BigDecimal amount, BigDecimal balance) {

}
