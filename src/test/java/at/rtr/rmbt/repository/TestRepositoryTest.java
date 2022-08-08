package at.rtr.rmbt.repository;

import at.rtr.rmbt.RTRApplication;
import at.rtr.rmbt.TestConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@ContextConfiguration(classes = {RTRApplication.class})
@Ignore
public class TestRepositoryTest extends AbstractRepositoryTest<TestRepository> {


    @Test
    public void getRmbtSetProviderFromAs_whenProviderExists_expectA1Telecom() {
        assertEquals(TestConstants.Database.PROVIDER_A1_TELECOM_SHORT_NAME, dao.getRmbtSetProviderFromAs(1L));
    }

    @Test
    public void save_whenNetworkOperatorNameHasCrLrCharacters_expectReplaced() {
        at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();
        test.setNetworkOperatorName("\r" + TestConstants.DEFAULT_TEXT + "\n");
        test.setUseSsl(false);


        var savedTest = dao.save(test);

        assertEquals(TestConstants.DEFAULT_TEXT, savedTest.getNetworkOperatorName());

        savedTest.setNetworkOperatorName("\r" + TestConstants.DEFAULT_TEXT + "\n");
        dao.flush();

        var updatedTest = dao.save(savedTest);

        assertEquals(TestConstants.DEFAULT_TEXT, updatedTest.getNetworkOperatorName());
    }

    @Test
    public void save_whenNetworkSimOperatorNameHasCrLrCharacters_expectReplaced() {
        at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();
        test.setNetworkSimOperatorName("\r" + TestConstants.DEFAULT_TEXT + "\n");
        test.setUseSsl(false);


        var savedTest = dao.save(test);

        assertEquals(TestConstants.DEFAULT_TEXT, savedTest.getNetworkSimOperatorName());

        savedTest.setNetworkSimOperatorName("\r" + TestConstants.DEFAULT_TEXT + "\n");
        dao.flush();

        var updatedTest = dao.save(savedTest);

        assertEquals(TestConstants.DEFAULT_TEXT, updatedTest.getNetworkSimOperatorName());
    }

    @Test
    public void save_whenPublicIpAsNameHasCrLrCharacters_expectReplaced() {
        at.rtr.rmbt.model.Test test = new at.rtr.rmbt.model.Test();
        test.setPublicIpAsName("\r" + TestConstants.DEFAULT_TEXT + "\n");
        test.setUseSsl(false);


        var savedTest = dao.save(test);

        assertEquals(TestConstants.DEFAULT_TEXT, savedTest.getPublicIpAsName());

        savedTest.setPublicIpAsName("\r" + TestConstants.DEFAULT_TEXT + "\n");
        dao.flush();

        var updatedTest = dao.save(savedTest);

        assertEquals(TestConstants.DEFAULT_TEXT, updatedTest.getPublicIpAsName());
    }
}
