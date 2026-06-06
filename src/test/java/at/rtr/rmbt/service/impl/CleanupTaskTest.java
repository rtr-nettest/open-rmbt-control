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
import org.springframework.jdbc.core.StatementCallback;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private Statement statement;

    private CleanupTaskProperties properties;
    private CleanupTask cleanupTask;

    @BeforeEach
    void setUp() {
        properties = new CleanupTaskProperties();
        cleanupTask = new CleanupTask(jdbcTemplate, properties);
    }

    @Test
    void run_executesAllConfiguredStatementsInOrder() throws SQLException {
        properties.setStatements(List.of("update a set x = null", "delete from b where y = 1"));
        // CleanupTask runs each statement via JdbcTemplate.execute(StatementCallback); drive the
        // callback with a mock Statement so we can assert the SQL was actually executed.
        when(jdbcTemplate.execute(any(StatementCallback.class))).thenAnswer(inv ->
                ((StatementCallback<?>) inv.getArgument(0)).doInStatement(statement));

        cleanupTask.run();

        final InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).execute("update a set x = null");
        inOrder.verify(statement).execute("delete from b where y = 1");
    }

    @Test
    void run_skipsNullAndBlankStatements() throws SQLException {
        properties.setStatements(Arrays.asList("   ", null, "update test set client_public_ip = null where uid = 1"));
        when(jdbcTemplate.execute(any(StatementCallback.class))).thenAnswer(inv ->
                ((StatementCallback<?>) inv.getArgument(0)).doInStatement(statement));

        cleanupTask.run();

        verify(statement).execute("update test set client_public_ip = null where uid = 1");
        verify(statement, times(1)).execute(anyString());
    }

    @Test
    void run_continuesAfterAStatementFails() {
        properties.setStatements(List.of("broken statement", "good statement"));
        // The first statement fails (JdbcTemplate translates the SQLException to a DataAccessException);
        // the task must catch it and still run the second statement.
        when(jdbcTemplate.execute(any(StatementCallback.class)))
                .thenThrow(new DataAccessResourceFailureException("db down"))
                .thenReturn(null);

        cleanupTask.run();

        verify(jdbcTemplate, times(2)).execute(any(StatementCallback.class));
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
