package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.Signal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignalRepository extends JpaRepository<Signal, Long> {
}
