package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.ProviderMapper;
import at.rtr.rmbt.model.Provider;
import at.rtr.rmbt.response.ProviderResponse;
import org.springframework.stereotype.Service;

/**
 * Provider mapper impl class.
 */
@Service
public class ProviderMapperImpl implements ProviderMapper {

    /**
     * Provider to provider response.
     *
     * @param provider the Provider
     * @return the result
     */
    @Override
    public ProviderResponse providerToProviderResponse(Provider provider) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .build();
    }
}
