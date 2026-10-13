package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.repository.SettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * In-memory copy of the {@code settings} table, so that reading a setting never causes a database
 * query. The table is loaded at startup and reloaded periodically ({@code app.settings.refreshRate},
 * default 10 minutes); a changed value therefore takes effect without a restart.
 * <p>
 * Rows with {@code lang} NULL or empty are language-neutral. If a key has several rows for the same
 * language, the NULL-language row and then the row with the lowest {@code uid} wins. Rows with a NULL
 * {@code value} are ignored.
 * <p>
 * This class is kept identical (apart from package and imports) in the control, statistics and map
 * server.
 */
@Slf4j
@Component
public class SettingsCache {

    /** Map key for language-neutral rows. */
    private static final String NEUTRAL = "";

    private final SettingsRepository settingsRepository;

    /** key -> (lang -> value); replaced as a whole on refresh. */
    private volatile Map<String, Map<String, String>> settings = Map.of();

    /** "key=value" of invalid numeric values already logged. */
    private final Set<String> invalidValuesLogged = ConcurrentHashMap.newKeySet();

    public SettingsCache(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * Reloads all settings. On a database error the previous values are kept. Synchronized so that a
     * periodic reload that started before a settings change cannot overwrite a later reload.
     */
    @PostConstruct
    @Scheduled(fixedRateString = "${app.settings.refreshRate:PT10M}", initialDelayString = "${app.settings.refreshRate:PT10M}")
    public synchronized void refresh() {
        final List<Settings> rows;
        try {
            rows = new ArrayList<>(settingsRepository.findAll());
        } catch (RuntimeException e) {
            log.warn("Could not load settings, keeping previous values", e);
            return;
        }
        // NULL-language rows before empty-language rows, then by uid: the first row wins
        rows.sort(Comparator.comparing((Settings s) -> s.getLang() != null)
                .thenComparing(Settings::getId, Comparator.nullsLast(Comparator.naturalOrder())));

        final Map<String, Map<String, String>> loaded = new HashMap<>();
        for (Settings row : rows) {
            // a row without value is treated as absent, so the next row / the neutral value applies
            if (row.getKey() == null || row.getValue() == null) {
                continue;
            }
            final String lang = StringUtils.isEmpty(row.getLang()) ? NEUTRAL : row.getLang();
            loaded.computeIfAbsent(row.getKey(), k -> new HashMap<>()).putIfAbsent(lang, row.getValue());
        }
        loaded.replaceAll((key, values) -> Collections.unmodifiableMap(values));
        settings = Collections.unmodifiableMap(loaded);
        log.debug("Loaded {} settings", loaded.size());
    }

    /**
     * Reloads the settings once the current transaction is committed (immediately if there is no
     * transaction). To be called after settings were written.
     */
    public void refreshAfterCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    refresh();
                }
            });
        } else {
            refresh();
        }
    }

    /**
     * @param key setting key
     * @return the language-neutral value
     */
    public Optional<String> get(String key) {
        return Optional.ofNullable(settings.getOrDefault(key, Map.of()).get(NEUTRAL));
    }

    /**
     * @param key  setting key
     * @param lang language, e.g. "de"
     * @return the value for the language, otherwise the language-neutral value
     */
    public Optional<String> get(String key, String lang) {
        final Map<String, String> values = settings.getOrDefault(key, Map.of());
        if (StringUtils.isNotEmpty(lang) && values.containsKey(lang)) {
            return Optional.ofNullable(values.get(lang));
        }
        return Optional.ofNullable(values.get(NEUTRAL));
    }

    /**
     * @param keys setting keys
     * @param lang language, see {@link #get(String, String)}
     * @return key -> value for all keys that have a value
     */
    public Map<String, String> getAll(Collection<String> keys, String lang) {
        final Map<String, String> result = new LinkedHashMap<>();
        for (String key : keys) {
            get(key, lang).ifPresent(value -> result.put(key, value));
        }
        return result;
    }

    /**
     * @return the language-neutral value as int, or {@code defaultValue} if missing or not a number
     */
    public int getInt(String key, int defaultValue) {
        return getNumber(key, defaultValue, Integer::valueOf);
    }

    /**
     * @return the language-neutral value as long, or {@code defaultValue} if missing or not a number
     */
    public long getLong(String key, long defaultValue) {
        return getNumber(key, defaultValue, Long::valueOf);
    }

    /**
     * @return the language-neutral value as double, or {@code defaultValue} if missing or not a finite
     * number
     */
    public double getDouble(String key, double defaultValue) {
        return getNumber(key, defaultValue, value -> {
            final double result = Double.parseDouble(value);
            if (!Double.isFinite(result)) { // "NaN", "Infinity" are parsed by Double
                throw new NumberFormatException("not finite: " + value);
            }
            return result;
        });
    }

    private <T> T getNumber(String key, T defaultValue, Function<String, T> parser) {
        final Optional<String> value = get(key);
        if (value.isEmpty()) {
            return defaultValue;
        }
        try {
            return parser.apply(value.get().trim());
        } catch (NumberFormatException e) {
            // warn once per invalid value, not on every read
            if (invalidValuesLogged.add(key + "=" + value.get())) {
                log.warn("Invalid value '{}' for setting {}, using default {}", value.get(), key, defaultValue);
            }
            return defaultValue;
        }
    }
}
