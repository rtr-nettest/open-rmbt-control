package com.rtr.nettest.repository;

import com.rtr.nettest.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository(ClientRepository.NAME)
public interface ClientRepository extends JpaRepository<Client, Long> {
    String NAME = "RtrClientRepository";

    Optional<Client> findByUuid(UUID uuid);
}
