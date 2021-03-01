package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.mapper.ProviderMapper;
import at.rtr.rmbt.repository.ProviderRepository;
import at.rtr.rmbt.response.ProviderResponse;
import at.rtr.rmbt.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Override
    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(providerMapper::providerToProviderResponse)
                .collect(Collectors.toList());
    }
}
