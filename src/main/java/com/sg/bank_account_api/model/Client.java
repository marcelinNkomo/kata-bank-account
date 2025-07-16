package com.sg.bank_account_api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "clients")
public record Client(@Id String id, String lastName, String firstName, LocalDate date) {

}
