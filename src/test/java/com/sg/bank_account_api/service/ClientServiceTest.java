package com.sg.bank_account_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sg.bank_account_api.dto.CreateClientDto;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private CreateClientDto createClientDto;
    private Client savedClient;

    @BeforeEach
    void setUp() {
        createClientDto = new CreateClientDto("Doe", "John");
        savedClient = new Client("client123", "Doe", "John", LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create client successfully")
    void shouldCreateClientSuccessfully() {
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        Client result = clientService.createClient(createClientDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("client123");
        assertThat(result.lastname()).isEqualTo("Doe");
        assertThat(result.firstname()).isEqualTo("John");
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating client with null DTO")
    void shouldThrowIllegalArgumentExceptionWhenCreateClientWithNullDto() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> clientService.createClient(null));

        assertThat(thrown.getMessage()).contains("Client can't be null and should have either lastname or fisrtname");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating client with blank lastname")
    void shouldThrowIllegalArgumentExceptionWhenCreateClientWithBlankLastname() {
        CreateClientDto invalidDto = new CreateClientDto("", "John");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> clientService.createClient(invalidDto));

        assertThat(thrown.getMessage()).contains("Client can't be null and should have either lastname or fisrtname");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating client with blank firstname")
    void shouldThrowIllegalArgumentExceptionWhenCreateClientWithBlankFirstname() {
        CreateClientDto invalidDto = new CreateClientDto("Doe", "");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> clientService.createClient(invalidDto));

        assertThat(thrown.getMessage()).contains("Client can't be null and should have either lastname or fisrtname");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should retrieve client by ID successfully")
    void shouldGetClientByIdSuccessfully() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(savedClient));

        Client result = clientService.getClientById("client123");

        assertThat(result).isEqualTo(savedClient);
        verify(clientRepository, times(1)).findById("client123");
    }

    @Test
    @DisplayName("Should throw AmountException when client by ID not found")
    void shouldThrowAmountExceptionWhenGetClientByIdNotFound() {
        when(clientRepository.findById("nonExistentClient")).thenReturn(Optional.empty());

        AmountException thrown = assertThrows(AmountException.class,
                () -> clientService.getClientById("nonExistentClient"));

        assertThat(thrown.getMessage()).contains("Client not found for ID : nonExistentClient");
        verify(clientRepository, times(1)).findById("nonExistentClient");
    }
}