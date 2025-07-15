package com.sg.bank_account_api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Statement(LocalDate date, BigDecimal amount, BigDecimal balance) {

}
