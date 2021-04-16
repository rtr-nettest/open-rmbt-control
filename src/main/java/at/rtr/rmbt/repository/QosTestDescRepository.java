package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.QosTestDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface QosTestDescRepository extends JpaRepository<QosTestDesc, Long> {
    @Query(
        value = "SELECT  DISTINCT " +
        "COALESCE(b.uid, a.uid) uid, " +
        "COALESCE(b.desc_key, a.desc_key) \"desc_key\", " +
        "COALESCE(b.value, a.value) \"value\", " +
        "COALESCE(b.lang, a.lang) lang " +
        "FROM qos_test_desc a " +
        "LEFT JOIN (" +
        " 	SELECT uid, desc_key, \"value\", lang " +
        " 	FROM qos_test_desc " +
        " 	WHERE lang = :lang " +
        ") b ON a.uid <> b.uid AND a.desc_key = b.desc_key " +
        "WHERE a.lang IN (:langs) AND a.desc_key IN (:keys) ",
        nativeQuery = true
    )
    List<QosTestDesc> findByKeysAndLocales(
        @Param("lang") String lang,
        @Param("langs") Set<String> langs,
        @Param("keys") Set<String> keys
    );
}
