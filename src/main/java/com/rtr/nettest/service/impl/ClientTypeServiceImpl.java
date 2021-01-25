package com.rtr.nettest.service.impl;

import com.rtr.nettest.model.ClientType;
import com.rtr.nettest.repository.ClientTypeRepository;
import com.rtr.nettest.service.ClientTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientTypeServiceImpl implements ClientTypeService {

    private final ClientTypeRepository clientTypeRepository;

    @Override
    public Optional<ClientType> findByClientType(com.rtr.nettest.model.enums.ClientType clientType) {
        return clientTypeRepository.findByClientType(clientType);
    }
}
