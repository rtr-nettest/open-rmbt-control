package com.rtr.nettest.service.impl;

import com.rtr.nettest.model.ClientType;
import com.rtr.nettest.exception.ClientNotFoundByNameException;
import com.rtr.nettest.repository.ClientTypeRepository;
import com.rtr.nettest.service.ClientTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientTypeServiceImpl implements ClientTypeService {

    private final ClientTypeRepository clientTypeRepository;

    @Override
    public ClientType getClientTypeByName(String name) {
        return clientTypeRepository.findByName(name)
                .orElseThrow(ClientNotFoundByNameException::new);
    }
}
