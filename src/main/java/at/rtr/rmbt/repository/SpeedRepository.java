package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.speed.Speed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpeedRepository extends JpaRepository<Speed, UUID> {
}
