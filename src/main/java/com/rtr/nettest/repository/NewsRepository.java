package com.rtr.nettest.repository;

import com.rtr.nettest.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query(value = "SELECT * FROM news  " +
            "WHERE (uid > :lastNewsUid OR force = true) AND active = true"
            + " AND (plattform IS NULL OR plattform = :platform)"
            + " AND (max_software_version_code IS NULL OR :softwareVersionCode <= max_software_version_code)"
            + " AND (min_software_version_code IS NULL OR :softwareVersionCode >= min_software_version_code)"
            + " AND (uuid IS NULL OR cast(uuid as TEXT) = :uuid)" +
            " ORDER BY time ASC", nativeQuery = true)
    List<News> findActiveByUidAndPlatformAndSoftwareVersionCodeAndUuid(Long lastNewsUid, String platform, Long softwareVersionCode, String uuid);
}
