package at.rtr.rmbt.service;

import at.rtr.rmbt.model.Test;

import java.util.List;

public interface TestService {
    Test save(Test test);

    String getRmbtSetProviderFromAs(Long testUid);

    Integer getRmbtNextTestSlot(Long testUid);

    List<String> getDeviceHistory(Long clientId);

    List<String> getGroupNameByClientId(Long clientId);

}
