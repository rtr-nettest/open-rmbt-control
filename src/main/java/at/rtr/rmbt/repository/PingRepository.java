package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Ping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PingRepository extends JpaRepository<Ping, Long> {
}
