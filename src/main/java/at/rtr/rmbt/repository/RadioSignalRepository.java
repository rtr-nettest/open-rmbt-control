package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RadioSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Radio signal repository interface.
 */
public interface RadioSignalRepository extends JpaRepository<RadioSignal, Long> {

    /**
     * Find all by cell UUID in order by time asc.
     *
     * @param cellUUIDs the Cell UUI ds
     * @return the result
     */
    List<RadioSignal> findAllByCellUUIDInOrderByTimeAsc(Collection<UUID> cellUUIDs);

    /**
     * Find max by cell UUID in.
     *
     * @param cellUUIDs the Cell UUI ds
     * @return the result
     */
    @Query(value = "SELECT MAX(r.timeNs) from RadioSignal r where r.cellUUID in (:cellUUIDs)")
    Optional<Long> findMaxByCellUUIDIn(Collection<UUID> cellUUIDs);

    /**
     * Find distinct network type id by cell UUID in.
     *
     * @param cellUUIDs the Cell UUI ds
     * @return the result
     */
    @Query(value = "SELECT DISTINCT (r.networkTypeId) from RadioSignal r where r.cellUUID in (:cellUUIDs)")
    List<Integer> findDistinctNetworkTypeIdByCellUUIDIn(Collection<UUID> cellUUIDs);
}

