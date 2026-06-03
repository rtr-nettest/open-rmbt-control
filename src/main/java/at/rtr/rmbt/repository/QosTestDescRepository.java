package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface QosTestDescRepository extends JpaRepository<QosTestDesc, Long> {
    /**
     * Returns exactly one row per {@code desc_key}: the requested language ({@code :lang}) when a
     * translation exists, otherwise a deterministic fallback among the supported languages
     * ({@code :langs}). The {@code DISTINCT ON (desc_key)} together with the {@code ORDER BY}
     * (requested language first, then language name) collapses the multi-language rows in SQL, so a
     * key that exists in several languages does not produce duplicate rows for the caller
     */
    @Query(
        value = "SELECT DISTINCT ON (desc_key) uid, desc_key, \"value\", lang " +
        "FROM qos_test_desc " +
        "WHERE desc_key IN (:keys) AND lang IN (:langs) " +
        "ORDER BY desc_key, (lang = :lang) DESC, lang ",
        nativeQuery = true
    )
    List<QosTestDesc> findByKeysAndLocales(
        @Param("lang") String lang,
        @Param("langs") Set<String> langs,
        @Param("keys") Set<String> keys
    );
}
