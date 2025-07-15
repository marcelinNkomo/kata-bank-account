package com.sg.bank_account_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientService service;

    /**
     * createClient
     */
    @Test
    void createClientShouldThrowExceptionWhenGivenIsNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> service.createClient(null));
        assertThat(thrown).hasMessage("Client can't be null and should have either lastname or fisrtname");
    }

    @Test
    void createClientShouldThrowExceptionWhenClientInfosAreNotValide() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> service.createClient(new Client()));
        assertThat(thrown).hasMessage("Client can't be null and should have either lastname or fisrtname");
    }

    @Test
    void createClientShouldReturnCreatedClient() {
        // Given
        Client newClient = new Client("Wick", "John");
        when(repository.save(any(Client.class))).thenReturn(newClient);

        // when
        Client createdClient = service.createClient(newClient);

        // Then
        assertThat(newClient).isEqualTo(createdClient);
    }

    /**
     * getClientById
     */
    @Test
    void getClientByIdShouldThrowException_whenClientNotFoundForGivenId() {
        // Given
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // When
        AmountException thrown = assertThrows(AmountException.class,
                () -> service.getClientById("fake_id"));
        assertThat(thrown).hasMessage("Client not found for ID : fake_id");
    }

    @Test
    void getClientByIdShouldClient_whenExist() {
        // Given
        Client expectedClient = new Client("Bauer", "Jack");
        when(repository.findById(anyString())).thenReturn(Optional.of(expectedClient));

        // When
        Client existingClient = service.getClientById("A007");

        // Then
        assertThat(existingClient).isEqualTo(expectedClient);
    }

}
