package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Ping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Ping repository interface.
 */
public interface PingRepository extends JpaRepository<Ping, Long> {
}
