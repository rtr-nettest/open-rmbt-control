package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Provider;
import at.rtr.rmbt.response.ProviderResponse;

/**
 * Provider mapper interface.
 */
public interface ProviderMapper {

    /**
     * Provider to provider response.
     *
     * @param provider the Provider
     * @return the result
     */
    ProviderResponse providerToProviderResponse(Provider provider);
}
