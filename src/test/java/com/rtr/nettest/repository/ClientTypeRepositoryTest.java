package com.rtr.nettest.repository;

import com.rtr.nettest.TestConstants;
import com.rtr.nettest.model.ClientType;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
public class ClientTypeRepositoryTest extends AbstractRepositoryTest<ClientTypeRepository> {

    @Autowired
    @Qualifier(ClientRepository.NAME)
    private ClientRepository clientRepository;

    @Test
    public void findByClientType_whenExists_expectClientFound() {
        Optional<ClientType> result = dao.findByClientType(com.rtr.nettest.model.enums.ClientType.DESKTOP);

        assertTrue(result.isPresent());
        assertEquals(com.rtr.nettest.model.enums.ClientType.DESKTOP, result.get().getClientType());
    }

    @Test
    public void findByClientType_whenNotExists_expectClientNotFound() {
        Optional<ClientType> clientType = dao.findByClientType(com.rtr.nettest.model.enums.ClientType.MOBILE);
        clientRepository.deleteById(TestConstants.Database.CLIENT_TYPE_MOBILE_UID);

        dao.delete(clientType.get());

        Optional<ClientType> result = dao.findByClientType(com.rtr.nettest.model.enums.ClientType.MOBILE);
        assertFalse(result.isPresent());
    }
}
