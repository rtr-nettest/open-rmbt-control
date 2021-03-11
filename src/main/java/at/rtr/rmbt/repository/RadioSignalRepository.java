package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RadioSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RadioSignalRepository extends JpaRepository<RadioSignal, Long> {

    List<RadioSignal> findAllByCellUUIDInOrderByTimeAsc(Collection<UUID> cellUUIDs);
}
