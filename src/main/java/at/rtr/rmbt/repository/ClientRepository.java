package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RtrClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository(ClientRepository.NAME)
public interface ClientRepository extends JpaRepository<RtrClient, Long> {
    String NAME = "RtrClientRepository";

    Optional<RtrClient> findByUuid(UUID uuid);
}
