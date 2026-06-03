package at.rtr.rmbt.service;

import at.rtr.rmbt.response.ProviderResponse;

import java.util.List;

/**
 * Provider service interface.
 */
public interface ProviderService {

    List<ProviderResponse> getAllProviders();
}
