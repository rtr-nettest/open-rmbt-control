package at.rtr.rmbt.repository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

// TODO set up docker on CI/CD machine to run DB tests
@RunWith(SpringRunner.class)
@Transactional
@Rollback
@EnableJpaRepositories(basePackages = "com.rtr.nettest.repository")
@ContextConfiguration(classes = TestDatabaseConfig.class)
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected T dao;
}
