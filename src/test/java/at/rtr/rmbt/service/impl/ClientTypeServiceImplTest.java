package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.ClientType;
import at.rtr.rmbt.repository.ClientTypeRepository;
import at.rtr.rmbt.service.ClientTypeService;
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
        when(clientTypeRepository.findByClientType(at.rtr.rmbt.model.enums.ClientType.DESKTOP)).thenReturn(Optional.of(clientType));

        var response = clientTypeService.findByClientType(at.rtr.rmbt.model.enums.ClientType.DESKTOP);

        assertEquals(clientType, response.get());
    }
}