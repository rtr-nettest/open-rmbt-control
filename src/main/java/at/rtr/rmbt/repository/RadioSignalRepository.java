package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RadioSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RadioSignalRepository extends JpaRepository<RadioSignal, Long> {

    List<RadioSignal> findAllByCellUUIDInOrderByTimeAsc(Collection<UUID> cellUUIDs);

    @Query(value = "SELECT MAX(r.timeNs) from RadioSignal r where r.cellUUID in (:cellUUIDs)")
    Optional<Long> findMaxByCellUUIDIn(Collection<UUID> cellUUIDs);

    @Query(value = "SELECT DISTINCT (r.networkTypeId) from RadioSignal r where r.cellUUID in (:cellUUIDs)")
    List<Integer> findDistinctNetworkTypeIdByCellUUIDIn(Collection<UUID> cellUUIDs);
}

