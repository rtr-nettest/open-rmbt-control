package at.rtr.rmbt.repository;

import at.rtr.rmbt.dto.TestDistance;

import java.util.UUID;

/**
 * Geo analytics repository interface.
 */
public interface GeoAnalyticsRepository {

    TestDistance getTestDistance(UUID openTestUUID);
}
