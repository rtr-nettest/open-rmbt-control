package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientTypeRepository extends JpaRepository<ClientType, Long> {
    Optional<ClientType> findByClientType(at.rtr.rmbt.model.enums.ClientType clientType);
}
