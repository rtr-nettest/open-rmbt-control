package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    List<Settings> findAllByLangOrLangIsNullAndKeyIn(String lang, Collection<String> keys);
}
