package com.sg.bank_account_api.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
public record Client(@Id String id, String lastname, String firstname, LocalDateTime date) {

}
