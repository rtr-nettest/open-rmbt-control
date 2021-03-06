package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query(value = "SELECT * FROM news  " +
        "WHERE (uid > :lastNewsUid OR force = true) AND active = true"
        + " AND (plattform IS NULL OR plattform = :platform)"
        + " AND (max_software_version_code IS NULL OR :softwareVersionCode <= max_software_version_code)"
        + " AND (min_software_version_code IS NULL OR :softwareVersionCode >= min_software_version_code)"
        + " AND (uuid IS NULL OR cast(uuid as TEXT) = :uuid)"
        + " AND (start_time IS NULL OR start_time <= NOW())"
        + " AND (end_time IS NULL OR end_time >= NOW())"
        + " ORDER BY time ASC", nativeQuery = true)
    List<News> findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(Long lastNewsUid, String platform, Long softwareVersionCode, String uuid);
}
