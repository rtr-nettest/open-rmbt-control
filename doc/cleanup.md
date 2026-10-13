# Scheduled Cleanup Task

The control server runs a small scheduled job, `CleanupTask`, that executes a configurable list of
SQL statements against the control database on a fixed daily schedule. It is meant for recurring
housekeeping (clearing/updating columns, deleting stale rows, etc.).

- Implementation: `at.rtr.rmbt.service.impl.CleanupTask`
- Configuration: `at.rtr.rmbt.properties.CleanupTaskProperties` (prefix `cleanup-task`)
- Scheduling is enabled by `@EnableScheduling` on `RTRApplication`.

## How it works

Once per run (default: daily at 04:00) the task executes every configured statement **in order**.
For each statement it logs the statement and the number of affected rows:

```
INFO  Cleanup task: executing [update test set client_public_ip = null where uid = 1]
INFO  Cleanup task: [update test set client_public_ip = null where uid = 1] affected 1 row(s)
```

A statement that fails is logged at `ERROR` and **skipped** — the remaining statements still run, and
the scheduler thread is never broken by a failure.

## Configuration

All settings live under the `cleanup-task` key. Defaults are in `src/main/resources/application.yml`:

```yaml
cleanup-task:
  cron: "0 0 4 * * *"   # when to run (see below)
  zone: ""              # time zone for the cron; empty = server's default
  statements:           # SQL statements executed, in order, on each run
    - "update test set client_public_ip = null where uid = 1"
```

Any of these can be overridden per deployment via the usual mechanisms (environment variable, JVM
system property, or Tomcat `conf/context.xml` `<Parameter>`), without changing the code.

### Configure the time

`cron` is a **Spring cron expression** with six fields: `second minute hour day-of-month month day-of-week`.

| Expression        | Runs                          |
|-------------------|-------------------------------|
| `0 0 4 * * *`     | every day at 04:00 (default)  |
| `0 30 2 * * *`    | every day at 02:30            |
| `0 0 3 * * SUN`   | every Sunday at 03:00         |
| `0 0 1 1 * *`     | the 1st of each month at 01:00|
| `-`               | disabled (task never fires)   |

`zone` controls the time zone the cron is evaluated in. Leave it empty to use the server's default
time zone. If the JVM runs in UTC but you want local time, set it explicitly, e.g.:

```yaml
cleanup-task:
  zone: "Europe/Vienna"
```

> Note: the cron schedule is read at startup. Change it (or the zone) and restart the application
> for the new schedule to take effect.

### Configure the SQL

`statements` is an ordered list. Add as many entries as needed; they run top to bottom on each fire:

```yaml
cleanup-task:
  statements:
    - "update test set client_public_ip = null where uid = 1"
    - "delete from some_table where created < now() - interval '90 days'"
```

Guidelines:

- Statements are executed as plain JDBC updates (`JdbcTemplate.update`) — use any valid SQL the
  control DB user is allowed to run (`UPDATE`, `DELETE`, `INSERT`, DDL, …).
- They are **not** parameterised; write complete, self-contained statements. Do not build statements
  from untrusted input.
- Empty or blank list entries are ignored.
- Each statement commits on its own; there is no shared transaction across the list.

### Disabling the task

Either set the cron to `-`:

```yaml
cleanup-task:
  cron: "-"
```

or provide an empty statement list (the task then logs "nothing to do" and returns).
