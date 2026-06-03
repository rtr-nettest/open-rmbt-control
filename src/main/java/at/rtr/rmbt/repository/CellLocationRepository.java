package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.CellLocation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Cell location repository interface.
 */
public interface CellLocationRepository extends JpaRepository<CellLocation, Long> {
}
