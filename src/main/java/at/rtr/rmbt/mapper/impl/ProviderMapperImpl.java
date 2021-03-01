package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.ProviderMapper;
import at.rtr.rmbt.model.Provider;
import at.rtr.rmbt.response.ProviderResponse;
import org.springframework.stereotype.Service;

@Service
public class ProviderMapperImpl implements ProviderMapper {

    @Override
    public ProviderResponse providerToProviderResponse(Provider provider) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .build();
    }
}
