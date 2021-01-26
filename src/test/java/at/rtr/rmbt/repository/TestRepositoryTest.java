package at.rtr.rmbt.repository;

import at.rtr.rmbt.TestConstants;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Ignore
public class TestRepositoryTest extends AbstractRepositoryTest<TestRepository> {

    @Test
    public void getRmbtSetProviderFromAs_whenProviderExists_expectA1Telecom() {
        assertEquals(TestConstants.Database.PROVIDER_A1_TELECOM_SHORT_NAME, dao.getRmbtSetProviderFromAs(1L));
    }
}
