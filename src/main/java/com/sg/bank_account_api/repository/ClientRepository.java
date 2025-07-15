package com.sg.bank_account_api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sg.bank_account_api.model.Client;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {

}
