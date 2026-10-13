package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Settings;
import at.rtr.rmbt.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsCacheTest {

    @Mock
    private SettingsRepository settingsRepository;

    private SettingsCache settingsCache;

    @BeforeEach
    void setUp() {
        settingsCache = new SettingsCache(settingsRepository);
    }

    private static Settings row(long id, String key, String lang, String value) {
        return Settings.builder().id(id).key(key).lang(lang).value(value).build();
    }

    private void load(Settings... rows) {
        when(settingsRepository.findAll()).thenReturn(List.of(rows));
        settingsCache.refresh();
    }

    @Test
    void emptyBeforeRefresh() {
        assertEquals(Optional.empty(), settingsCache.get("system_UUID"));
        verifyNoInteractions(settingsRepository);
    }

    @Test
    void get_neutralValue() {
        load(row(1, "system_UUID", null, "abc"));
        assertEquals(Optional.of("abc"), settingsCache.get("system_UUID"));
        assertEquals(Optional.of("abc"), settingsCache.get("system_UUID", "de"));
    }

    @Test
    void get_emptyLangIsNeutral() {
        load(row(1, "system_UUID", "", "abc"));
        assertEquals(Optional.of("abc"), settingsCache.get("system_UUID"));
    }

    @Test
    void get_nullLangWinsOverEmptyLang() {
        load(row(1, "k", "", "empty"), row(2, "k", null, "null"));
        assertEquals(Optional.of("null"), settingsCache.get("k"));
    }

    @Test
    void get_duplicateRows_lowestUidWins() {
        load(row(5, "k", null, "second"), row(3, "k", null, "first"));
        assertEquals(Optional.of("first"), settingsCache.get("k"));
    }

    @Test
    void get_languageSpecificValueWithFallback() {
        load(row(1, "url_share", null, "neutral"), row(2, "url_share", "de", "german"));
        assertEquals(Optional.of("german"), settingsCache.get("url_share", "de"));
        assertEquals(Optional.of("neutral"), settingsCache.get("url_share", "en"));
        assertEquals(Optional.of("neutral"), settingsCache.get("url_share"));
    }

    @Test
    void get_languageOnly_notReturnedAsNeutral() {
        load(row(1, "tc_url", "en", "english"));
        assertEquals(Optional.empty(), settingsCache.get("tc_url"));
        assertEquals(Optional.of("english"), settingsCache.get("tc_url", "en"));
    }

    @Test
    void getAll_onlyKeysWithValue() {
        load(row(1, "a", null, "1"), row(2, "b", "de", "2"), row(3, "c", null, null));
        assertEquals(Map.of("a", "1", "b", "2"), settingsCache.getAll(List.of("a", "b", "c", "d"), "de"));
    }

    @Test
    void numbers_parsedOrDefault() {
        load(row(1, "int", null, " 5 "), row(2, "double", null, "2000.5"), row(3, "invalid", null, "abc"));
        assertEquals(5, settingsCache.getInt("int", 3));
        assertEquals(5L, settingsCache.getLong("int", 3L));
        assertEquals(2000.5, settingsCache.getDouble("double", 1.0));
        assertEquals(3, settingsCache.getInt("invalid", 3));
        assertEquals(3, settingsCache.getInt("missing", 3));
    }

    @Test
    void nullValue_ignored() {
        load(row(1, "k", null, null), row(2, "k", "", "fallback"), row(3, "k", "de", null));
        assertEquals(Optional.of("fallback"), settingsCache.get("k"));
        assertEquals(Optional.of("fallback"), settingsCache.get("k", "de"));
    }

    @Test
    void getDouble_notFinite_default() {
        load(row(1, "nan", null, "NaN"), row(2, "inf", null, "Infinity"));
        assertEquals(1.0, settingsCache.getDouble("nan", 1.0));
        assertEquals(1.0, settingsCache.getDouble("inf", 1.0));
    }

    @Test
    void reads_noDatabaseQuery() {
        load(row(1, "k", null, "v"));
        for (int i = 0; i < 100; i++) {
            settingsCache.get("k");
            settingsCache.getInt("k", 0);
        }
        verify(settingsRepository, times(1)).findAll();
    }

    @Test
    void refresh_picksUpChanges() {
        load(row(1, "k", null, "old"));
        load(row(1, "k", null, "new"));
        assertEquals(Optional.of("new"), settingsCache.get("k"));
    }

    @Test
    void refresh_databaseError_keepsPreviousValues() {
        load(row(1, "k", null, "v"));
        when(settingsRepository.findAll()).thenThrow(new RuntimeException("db down"));
        settingsCache.refresh();
        assertEquals(Optional.of("v"), settingsCache.get("k"));
    }

    @Test
    void refreshAfterCommit_withoutTransaction_refreshesImmediately() {
        when(settingsRepository.findAll()).thenReturn(List.of(row(1, "k", null, "v")));
        settingsCache.refreshAfterCommit();
        assertEquals(Optional.of("v"), settingsCache.get("k"));
    }
}
