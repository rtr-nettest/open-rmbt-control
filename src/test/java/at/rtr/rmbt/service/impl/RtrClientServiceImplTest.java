package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.repository.ClientRepository;
import at.rtr.rmbt.service.ClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RtrClientServiceImplTest {
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;

    @Mock
    private RtrClient rtrClient;
    @Mock
    private RtrClient savedRtrClient;

    @Before
    public void setUp() {
        clientService = new ClientServiceImpl(clientRepository);
    }

    @Test
    public void getClientByUUID_whenClientExist_expectClient() {
        when(clientRepository.findByUuid(TestConstants.DEFAULT_CLIENT_UUID)).thenReturn(Optional.of(rtrClient));

        var response = clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID);

        assertEquals(rtrClient, response);
    }

    @Test
    public void getClientByUUID_whenClientNotExist_expectNull() {
        var response = clientService.getClientByUUID(TestConstants.DEFAULT_CLIENT_UUID);

        assertNull(response);
    }

    @Test
    public void saveClient_whenCommonData_expect() {
        when(clientRepository.save(rtrClient)).thenReturn(savedRtrClient);

        var response = clientService.saveClient(rtrClient);

        assertEquals(savedRtrClient, response);
    }
}