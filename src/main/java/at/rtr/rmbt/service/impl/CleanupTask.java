package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.properties.CleanupTaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.util.List;

/**
 * Scheduled database cleanup task.
 *
 * <p>Runs once a day at 04:00 (configurable) and executes the configured SQL statements in order.
 * The schedule and the statement list come from {@link CleanupTaskProperties} / the
 * {@code cleanup-task} configuration. Each statement is run via {@link #executeAndLog} — which uses
 * {@code execute()} (not {@code update()}) so value-returning calls such as
 * {@code SELECT rmbt_purge_obsolete(...)} work, and logs the statement's result plus any server
 * messages (PostgreSQL {@code NOTICE}). A failing statement is logged at error and skipped, so the
 * remaining statements still run and the scheduler thread is never broken.
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
        try {
            executeAndLog(jdbcTemplate, sql);
        } catch (DataAccessException e) {
            // Log the failing SQL and the concrete DB error message
            log.error("Cleanup task: statement failed [{}]: {}", sql, e.getMostSpecificCause().getMessage());
        }
    }

    /**
     * Executes an arbitrary cleanup statement and logs its outcome. Uses {@code execute()} rather
     * than {@code update()} so value-returning calls such as {@code SELECT rmbt_purge_obsolete(...)}
     * work — {@code update()} runs {@code executeUpdate()}, which rejects a returned result with
     * "A result was returned when none was expected". Server-side messages (PostgreSQL {@code NOTICE},
     * e.g. from {@code RAISE NOTICE}) and the statement's result (a returned value, or the affected
     * row count for DML) are logged.
     */
    private void executeAndLog(final JdbcTemplate jdbc, final String sql) {
        log.debug("Cleanup task: executing [{}]", sql);
        jdbc.execute((StatementCallback<Void>) stmt -> {
            final boolean hasResultSet = stmt.execute(sql);
            for (SQLWarning w = stmt.getWarnings(); w != null; w = w.getNextWarning()) {
                log.info("Cleanup task [{}]: {}", sql, w.getMessage());
            }
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        log.info("Cleanup task [{}]: returned {}", sql, rs.getObject(1));
                    }
                }
            } else {
                log.info("Cleanup task [{}]: {} row(s) affected", sql, stmt.getUpdateCount());
            }
            return null;
        });
    }
}
