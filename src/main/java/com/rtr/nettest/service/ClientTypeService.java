package com.rtr.nettest.service;

import com.rtr.nettest.model.ClientType;

import java.util.Optional;

public interface ClientTypeService {
    Optional<ClientType> findByClientType(com.rtr.nettest.model.enums.ClientType clientType);
}
