package com.rtr.nettest.repository;

import com.rtr.nettest.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUuid(UUID uuid);
}
