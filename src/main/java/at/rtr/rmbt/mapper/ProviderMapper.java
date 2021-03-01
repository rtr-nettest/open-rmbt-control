package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.Provider;
import at.rtr.rmbt.response.ProviderResponse;

public interface ProviderMapper {

    ProviderResponse providerToProviderResponse(Provider provider);
}
