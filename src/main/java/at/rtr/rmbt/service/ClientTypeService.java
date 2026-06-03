package at.rtr.rmbt.service;

import at.rtr.rmbt.model.ClientType;

import java.util.Optional;

/**
 * Client type service interface.
 */
public interface ClientTypeService {
    /**
     * Find by client type.
     *
     * @param clientType the Client type
     * @return the result
     */
    Optional<ClientType> findByClientType(at.rtr.rmbt.enums.ClientType clientType);
}
