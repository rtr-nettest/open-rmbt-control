package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.repository.ClientRepository;
import at.rtr.rmbt.service.ClientService;
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
    public RtrClient getClientByUUID(UUID uuid) {
        return clientRepository.findByUuid(uuid)
                .orElse(null);
    }

    @Override
    public RtrClient saveClient(RtrClient rtrClient) {
        return clientRepository.save(rtrClient);
    }
}
