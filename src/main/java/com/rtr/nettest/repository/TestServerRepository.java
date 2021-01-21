package com.rtr.nettest.repository;

import com.rtr.nettest.model.TestServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TestServerRepository extends JpaRepository<TestServer, Long> {

    List<TestServer> getByActiveTrueAndSelectableTrueAndServerTypeIn(Collection<String> serverTypes);
}
