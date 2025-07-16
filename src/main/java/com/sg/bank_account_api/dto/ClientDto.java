package com.sg.bank_account_api.dto;

import java.time.LocalDateTime;

public record ClientDto(String id, String lastname, String firstname, LocalDateTime date) {

}
