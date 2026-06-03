package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.properties.CleanupTaskProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CleanupTask}: it runs every configured statement in order, tolerates blank
 * entries and per-statement failures, and does nothing when no statements are configured.
 */
@ExtendWith(MockitoExtension.class)
class CleanupTaskTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private CleanupTaskProperties properties;
    private CleanupTask cleanupTask;

    @BeforeEach
    void setUp() {
        properties = new CleanupTaskProperties();
        cleanupTask = new CleanupTask(jdbcTemplate, properties);
    }

    @Test
    void run_executesAllConfiguredStatementsInOrder() {
        properties.setStatements(List.of("update a set x = null", "delete from b where y = 1"));
        when(jdbcTemplate.update("update a set x = null")).thenReturn(1);
        when(jdbcTemplate.update("delete from b where y = 1")).thenReturn(3);

        cleanupTask.run();

        final InOrder inOrder = inOrder(jdbcTemplate);
        inOrder.verify(jdbcTemplate).update("update a set x = null");
        inOrder.verify(jdbcTemplate).update("delete from b where y = 1");
    }

    @Test
    void run_skipsNullAndBlankStatements() {
        properties.setStatements(Arrays.asList("   ", null, "update test set client_public_ip = null where uid = 1"));
        when(jdbcTemplate.update("update test set client_public_ip = null where uid = 1")).thenReturn(1);

        cleanupTask.run();

        verify(jdbcTemplate).update("update test set client_public_ip = null where uid = 1");
        verify(jdbcTemplate, times(1)).update(anyString());
    }

    @Test
    void run_continuesAfterAStatementFails() {
        properties.setStatements(List.of("broken statement", "good statement"));
        when(jdbcTemplate.update("broken statement")).thenThrow(new DataAccessResourceFailureException("db down"));
        when(jdbcTemplate.update("good statement")).thenReturn(2);

        cleanupTask.run();

        verify(jdbcTemplate).update("broken statement");
        verify(jdbcTemplate).update("good statement");
    }

    @Test
    void run_withNoStatements_doesNothing() {
        properties.setStatements(List.of());

        cleanupTask.run();

        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void defaultStatementTargetsClientPublicIp() {
        assertTrue(new CleanupTaskProperties().getStatements().get(0).contains("client_public_ip"));
    }
}
