package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Long> {

    @Query("select s from Settings s where (s.lang is null  or  s.lang = :lang) and s.key in :keys")
    List<Settings> findAllByLangOrLangIsNullAndKeyIn(String lang, Collection<String> keys);

    Optional<Settings> findFirstByKeyAndLangIsNullOrKeyAndLangOrderByLang(String key, String key2, String lang);
}
