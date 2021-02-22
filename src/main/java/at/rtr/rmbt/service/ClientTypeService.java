package at.rtr.rmbt.service;

import at.rtr.rmbt.model.ClientType;

import java.util.Optional;

public interface ClientTypeService {
    Optional<ClientType> findByClientType(at.rtr.rmbt.enums.ClientType clientType);
}
