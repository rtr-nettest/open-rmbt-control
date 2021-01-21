package com.rtr.nettest.service.impl;

import com.rtr.nettest.exception.ClientNotFoundByNameException;
import com.rtr.nettest.model.ClientType;
import com.rtr.nettest.repository.ClientTypeRepository;
import com.rtr.nettest.service.ClientTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.rtr.nettest.TestConstants.DEFAULT_CLIENT_TYPE_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ClientTypeServiceImplTest {
    private ClientTypeService clientTypeService;

    @MockBean
    private ClientTypeRepository clientTypeRepository;

    @Mock
    private ClientType clientType;

    @Before
    public void setUp() {
        clientTypeService = new ClientTypeServiceImpl(clientTypeRepository);
    }

    @Test
    public void getClientTypeByName_whenValidClientTypeName_expectClientType() {
        when(clientTypeRepository.findByName(DEFAULT_CLIENT_TYPE_NAME)).thenReturn(Optional.of(clientType));

        var response = clientTypeService.getClientTypeByName(DEFAULT_CLIENT_TYPE_NAME);

        assertEquals(clientType, response);
    }

    @Test(expected = ClientNotFoundByNameException.class)
    public void getClientTypeByName_whenInvalidClientTypeName_expectClientType() {

        clientTypeService.getClientTypeByName(DEFAULT_CLIENT_TYPE_NAME);
    }
}