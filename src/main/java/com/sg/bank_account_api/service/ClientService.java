package com.sg.bank_account_api.service;

import com.sg.bank_account_api.dto.CreateClientDto;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public final class ClientService implements IClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(CreateClientDto createClientDto) {
        validate(createClientDto);
        Client newClient = new Client(null, createClientDto.lastname(), createClientDto.firstname(), LocalDate.now());
        return clientRepository.save(newClient);
    }

    @Override
    public Client getClientById(String id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new AmountException("Client not found for ID : " + id));
    }

    private void validate(CreateClientDto createClientDto) {
        if (createClientDto == null || isBlank(createClientDto.lastname()) || isBlank(createClientDto.firstname())) {
            throw new IllegalArgumentException("Client can't be null and should have either lastname or fisrtname");
        }
    }
}
