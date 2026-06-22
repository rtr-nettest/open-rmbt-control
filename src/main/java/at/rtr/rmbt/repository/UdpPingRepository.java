package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.UdpPing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UdpPingRepository extends JpaRepository<UdpPing, Long> {
}
