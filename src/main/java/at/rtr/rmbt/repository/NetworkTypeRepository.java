package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.NetworkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface NetworkTypeRepository extends JpaRepository<NetworkType, Long> {

    @Query(value = "SELECT * " +
            " FROM" +
            " (SELECT s.network_type_id" +
            "  FROM signal s" +
            "  WHERE s.open_test_uuid= :openTestUUID" +
            "  UNION" +
            "  SELECT s1.network_type_id" +
            "  FROM radio_cell c" +
            "   JOIN radio_signal s1 ON c.uuid = s1.cell_uuid" +
            "  WHERE s1.open_test_uuid = :openTestUUID) s2" +
            " JOIN network_type nt" +
            "  ON nt.uid = s2.network_type_id" +
            " ORDER BY technology_order DESC" +
            " LIMIT 1", nativeQuery = true)
    Optional<NetworkType> findTopByOpenTestUUIDAndOrderByTechnologyOrderDesc(UUID openTestUUID);

    @Query(value = "SELECT * FROM (" +
            "   SELECT array_agg(DISTINCT group_name" +
            "          ORDER BY group_name) agg" +
            "   FROM (" +
            "         SELECT nt.group_name" +
            "         FROM" +
            "          (SELECT s.network_type_id" +
            "           FROM signal s" +
            "           WHERE s.open_test_uuid = :openTestUUID" +
            "           UNION" +
            "           SELECT s1.network_type_id" +
            "           FROM radio_cell c" +
            "            JOIN radio_signal s1 ON c.uuid = s1.cell_uuid" +
            "           WHERE s1.open_test_uuid = :openTestUUID) s2" +
            "          JOIN network_type nt" +
            "           ON nt.uid = s2.network_type_id) s3" +
            "  ) agg" +
            " JOIN network_type nt ON nt.aggregate=agg.agg", nativeQuery = true)
    Optional<NetworkType> findByOpenTestUUIDAndAggregate(UUID openTestUUID);
}
