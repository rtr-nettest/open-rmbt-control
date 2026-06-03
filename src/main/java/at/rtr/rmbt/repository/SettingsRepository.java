package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Settings repository interface.
 */
public interface SettingsRepository extends JpaRepository<Settings, Long> {

    /**
     * Find all by lang or lang is null and key in.
     *
     * @param lang the Lang
     * @param keys the Keys
     * @return the result
     */
    @Query("select s from Settings s where (s.lang is null  or  s.lang = :lang) and s.key in :keys")
    List<Settings> findAllByLangOrLangIsNullAndKeyIn(String lang, Collection<String> keys);

    /**
     * Find first by key and lang is null or key and lang order by lang.
     *
     * @param key the Key
     * @param key2 the Key 2
     * @param lang the Lang
     * @return the result
     */
    Optional<Settings> findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(String key, String key2, String lang);
}
