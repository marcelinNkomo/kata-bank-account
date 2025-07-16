package com.sg.bank_account_api.service;

import com.sg.bank_account_api.dto.CreateClientDto;
import com.sg.bank_account_api.model.Client;

public sealed interface IClientService permits ClientService {

    /**
     * permet de créer un nouveau client
     *
     * @param client
     * @return Client
     */
    Client createClient(CreateClientDto client);

    /**
     * permet de récuperer un client à partir de son identifiant
     *
     * @param id
     * @return Client
     */
    Client getClientById(String id);
}
