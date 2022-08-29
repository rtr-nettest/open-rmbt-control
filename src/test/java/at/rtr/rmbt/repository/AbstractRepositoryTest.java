package at.rtr.rmbt.repository;

import at.rtr.rmbt.RTRApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

// TODO set up docker on CI/CD machine to run DB tests
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RTRApplication.class, TestDatabaseConfig.class})
@DataJpaTest
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected T dao;
}
