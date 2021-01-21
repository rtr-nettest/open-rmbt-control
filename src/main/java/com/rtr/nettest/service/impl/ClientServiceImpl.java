package com.rtr.nettest.service.impl;

import com.rtr.nettest.model.Client;
import com.rtr.nettest.repository.ClientRepository;
import com.rtr.nettest.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    @Qualifier(ClientRepository.NAME)
    private final ClientRepository clientRepository;

    @Override
    public Client getClientByUUID(UUID uuid) {
        return clientRepository.findByUuid(uuid)
                .orElse(null);
    }

    @Override
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }
}
