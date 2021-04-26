package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.*;
import at.rtr.rmbt.response.*;

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

    ResultUpdateResponse updateTestResult(ResultUpdateRequest resultUpdateRequest);

    ImplausibleResponse setImplausible(ImplausibleRequest implausibleRequest);
}
