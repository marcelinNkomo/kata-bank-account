package com.sg.bank_account_api.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "clients")
public class Client {
    @Id
    private String id;
    private String lastName;
    private String firstName;
    private LocalDate date;

    public Client(String lastName, String firstName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = LocalDate.now();
    }

    // Utilisées principalement pour les tests
    public Client(String id, String lastName, String firstName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = LocalDate.now();
    }
}
