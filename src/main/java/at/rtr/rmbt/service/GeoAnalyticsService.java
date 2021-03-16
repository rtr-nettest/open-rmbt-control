package at.rtr.rmbt.service;

import at.rtr.rmbt.dto.TestDistance;

import java.util.UUID;

public interface GeoAnalyticsService {

    TestDistance getTestDistance(UUID openTestUUID);
}
