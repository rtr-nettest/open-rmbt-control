package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.ClientType;
import at.rtr.rmbt.TestConstants;
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
        Optional<ClientType> result = dao.findByClientType(at.rtr.rmbt.enums.ClientType.DESKTOP);

        assertTrue(result.isPresent());
        assertEquals(at.rtr.rmbt.enums.ClientType.DESKTOP, result.get().getClientType());
    }

    @Test
    public void findByClientType_whenNotExists_expectClientNotFound() {
        Optional<ClientType> clientType = dao.findByClientType(at.rtr.rmbt.enums.ClientType.MOBILE);
        clientRepository.deleteById(TestConstants.Database.CLIENT_TYPE_MOBILE_UID);

        dao.delete(clientType.get());

        Optional<ClientType> result = dao.findByClientType(at.rtr.rmbt.enums.ClientType.MOBILE);
        assertFalse(result.isPresent());
    }
}
