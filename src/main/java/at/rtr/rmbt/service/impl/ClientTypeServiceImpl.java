package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.model.ClientType;
import at.rtr.rmbt.repository.ClientTypeRepository;
import at.rtr.rmbt.service.ClientTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientTypeServiceImpl implements ClientTypeService {

    private final ClientTypeRepository clientTypeRepository;

    @Override
    public Optional<ClientType> findByClientType(at.rtr.rmbt.model.enums.ClientType clientType) {
        return clientTypeRepository.findByClientType(clientType);
    }
}
