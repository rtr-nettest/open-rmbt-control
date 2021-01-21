package com.rtr.nettest.repository;

import com.rtr.nettest.model.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientTypeRepository extends JpaRepository<ClientType, Long> {

    Optional<ClientType> findByName(String name);
}
