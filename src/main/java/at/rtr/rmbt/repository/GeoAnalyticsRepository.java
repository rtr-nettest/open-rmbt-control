package at.rtr.rmbt.repository;

import at.rtr.rmbt.dto.TestDistance;

import java.util.UUID;

public interface GeoAnalyticsRepository {

    TestDistance getTestDistance(UUID openTestUUID);
}
