package com.sg.bank_account_api.dto;

import java.math.BigDecimal;

public record TransactionDto(String clientId, String accountId, BigDecimal amount) {

}
