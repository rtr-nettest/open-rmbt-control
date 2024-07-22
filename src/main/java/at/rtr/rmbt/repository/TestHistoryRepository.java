package at.rtr.rmbt.repository;

import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.model.TestHistory;

import java.util.List;

public interface TestHistoryRepository {

    List<TestHistory> getTestHistoryByDevicesAndNetworksAndClient(
            Integer resultLimit,
            Integer resultOffset,
            List<String> devices,
            List<String> networks,
            RtrClient client,
            boolean includeFailedTests
    );
}
