package com.rtr.nettest.service.impl;

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
        when(clientTypeRepository.findByClientType(com.rtr.nettest.model.enums.ClientType.DESKTOP)).thenReturn(Optional.of(clientType));

        var response = clientTypeService.findByClientType(com.rtr.nettest.model.enums.ClientType.DESKTOP);

        assertEquals(clientType, response.get());
    }
}