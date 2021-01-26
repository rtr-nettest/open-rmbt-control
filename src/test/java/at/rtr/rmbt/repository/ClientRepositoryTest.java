package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.ClientType;
import at.rtr.rmbt.model.RtrClient;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Ignore
public class ClientRepositoryTest extends AbstractRepositoryTest<ClientRepository> {

    @Autowired
    private ClientTypeRepository clientTypeRepository;

    @Test
    public void save_expectClientSaved() {
        ClientType clientType = clientTypeRepository.findById(1L).get();
        RtrClient client = new RtrClient();
        client.setBlacklisted(false);
        client.setUuid(UUID.randomUUID());
        client.setClientType(clientType);
        client.setTime(ZonedDateTime.now());
        client.setTermsAndConditionsAccepted(true);
        client.setSyncCodeTimestamp(ZonedDateTime.now());
        client.setBlacklisted(false);
        client.setTermsAndConditionsAcceptedVersion(1L);
        client.setLastSeen(ZonedDateTime.now());
        client.setTermsAndConditionsAcceptedTimestamp(ZonedDateTime.now());

        long count = dao.count();
        client = dao.save(client);

        assertNotNull(client.getUid());
        assertEquals(++count, dao.count());
    }

    @Test
    public void deleteById_whenClientExists_expectClientDeleted() {
        long count = dao.count();
        dao.deleteById(1L);
        assertEquals(--count, dao.count());
    }
}
