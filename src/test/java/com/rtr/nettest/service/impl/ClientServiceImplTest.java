package com.rtr.nettest.service.impl;

import com.rtr.nettest.model.Client;
import com.rtr.nettest.repository.ClientRepository;
import com.rtr.nettest.service.ClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.rtr.nettest.TestConstants.DEFAULT_CLIENT_UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ClientServiceImplTest {
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;

    @Mock
    private Client client;
    @Mock
    private Client savedClient;

    @Before
    public void setUp() {
        clientService = new ClientServiceImpl(clientRepository);
    }

    @Test
    public void getClientByUUID_whenClientExist_expectClient() {
        when(clientRepository.findByUuid(DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(client));

        var response = clientService.getClientByUUID(DEFAULT_CLIENT_UUID);

        assertEquals(client, response);
    }

    @Test
    public void getClientByUUID_whenClientNotExist_expectNull() {
        var response = clientService.getClientByUUID(DEFAULT_CLIENT_UUID);

        assertNull(response);
    }

    @Test
    public void saveClient_whenCommonData_expect() {
        when(clientRepository.save(client)).thenReturn(savedClient);

        var response = clientService.saveClient(client);

        assertEquals(savedClient, response);
    }
}