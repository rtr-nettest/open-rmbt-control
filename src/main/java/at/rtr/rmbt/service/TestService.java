package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.HistoryRequest;
import at.rtr.rmbt.request.TestResultDetailRequest;
import at.rtr.rmbt.request.TestResultRequest;
import at.rtr.rmbt.response.HistoryResponse;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.response.TestResultContainerResponse;
import at.rtr.rmbt.response.TestResultDetailResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestService {
    Test save(Test test);

    String getRmbtSetProviderFromAs(Long testUid);

    Integer getRmbtNextTestSlot(Long testUid);

    List<String> getDeviceHistory(List<Long> clientIds);

    List<String> getGroupNameByClientIds(List<Long> clientIds);

    TestResponse getTestByUUID(UUID testUUID);

    Optional<Test> getByOpenTestUuid(UUID openTestUuid);

    Optional<Test> getByOpenTestUuidAndClientId(UUID openTestUUID, UUID clientUUID);

    TestResultDetailResponse getTestResultDetailByTestUUID(TestResultDetailRequest testResultDetailRequest);

    TestResultContainerResponse getTestResult(TestResultRequest testResultRequest);

    HistoryResponse getHistory(HistoryRequest historyRequest);
}
