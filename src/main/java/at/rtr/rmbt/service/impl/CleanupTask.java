package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.properties.CleanupTaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Scheduled database cleanup task.
 *
 * <p>Runs once a day at 04:00 (configurable) and executes the configured SQL statements in order.
 * The schedule and the statement list come from {@link CleanupTaskProperties} / the
 * {@code cleanup-task} configuration. Each statement is logged before execution together with the
 * number of affected rows; a failing statement is logged and skipped so the remaining statements
 * still run and the scheduler thread is never broken.
 */
@Slf4j
@Service
public class CleanupTask {

    private final JdbcTemplate jdbcTemplate;
    private final CleanupTaskProperties properties;

    public CleanupTask(final JdbcTemplate jdbcTemplate, final CleanupTaskProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    @Scheduled(
            cron = "${cleanup-task.cron:0 0 4 * * *}",
            zone = "${cleanup-task.zone:}")
    public void run() {
        final List<String> statements = properties.getStatements();
        if (statements == null || statements.isEmpty()) {
            log.info("Cleanup task: no statements configured, nothing to do");
            return;
        }
        for (final String statement : statements) {
            if (statement == null || statement.isBlank()) {
                continue;
            }
            executeStatement(statement.trim());
        }
    }

    private void executeStatement(final String sql) {
        log.info("Cleanup task: executing [{}]", sql);
        try {
            final int rows = jdbcTemplate.update(sql);
            log.info("Cleanup task: [{}] affected {} row(s)", sql, rows);
        } catch (DataAccessException e) {
            log.error("Cleanup task: statement failed [{}]", sql, e);
        }
    }
}
