package com.sg.bank_account_api.dto;

import java.time.LocalDate;

public record ClientDto(String id, String lastName, String firstName, LocalDate date) {

}
