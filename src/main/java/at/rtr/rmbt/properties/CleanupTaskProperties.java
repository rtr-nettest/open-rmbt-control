package at.rtr.rmbt.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration for the scheduled {@code cleanup-task}. All values can be overridden in
 * {@code application.yml} (or via the deployment context) under the {@code cleanup-task} prefix.
 */
@Getter
@Setter
@ConfigurationProperties("cleanup-task")
public class CleanupTaskProperties {

    /** Spring cron expression (sec min hour day month weekday); defaults to daily at 04:00. */
    private String cron = "0 0 4 * * *";

    /** Time zone for the cron; empty means the server's default time zone. */
    private String zone = "";

    /** SQL statements executed, in order, on each run. */
    private List<String> statements = List.of("update test set client_public_ip = null where uid = 1");
}
