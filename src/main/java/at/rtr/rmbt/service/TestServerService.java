package at.rtr.rmbt.service;

import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.enums.ServerType;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Test server service interface.
 */
public interface TestServerService {
    /**
     * Find by uuid and active.
     *
     * @param preferServer the Prefer server
     * @param active the Active
     * @return the result
     */
    Optional<TestServer> findByUuidAndActive(UUID preferServer, boolean active);

    /**
     * Find active by server type in and country.
     *
     * @param serverTypes the Server types
     * @param countries the Countries
     * @param coverage the Coverage
     * @return the result
     */
    TestServer findActiveByServerTypeInAndCountry(List<ServerType> serverTypes, String countries, Boolean coverage);

    List<TestServerResponseForSettings> getServers();

    List<TestServerResponseForSettings> getServersHttp();

    List<TestServerResponseForSettings> getServersWs();

    List<TestServerResponseForSettings> getServersQos();

    List<TestServerResponseForSettings> getServersUdp();

}
