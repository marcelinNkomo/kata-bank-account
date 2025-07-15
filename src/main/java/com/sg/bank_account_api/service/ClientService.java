package com.sg.bank_account_api.service;

import static org.apache.logging.log4j.util.Strings.isBlank;
import org.springframework.stereotype.Service;

import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.repository.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ClientService implements IClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(Client client) {
        validate(client);
        return clientRepository.save(client);
    }

    @Override
    public Client getClientById(String id) {
        return clientRepository.findById(id)
        .orElseThrow(() -> new AmountException("Client not found for ID : " + id));
    }
    
    private void validate(Client client) {
        if (client == null || isBlank(client.getLastName()) || isBlank(client.getFirstName())) {
            throw new IllegalArgumentException("Client can't be null and should have either lastname or fisrtname");
        }
    }
}
