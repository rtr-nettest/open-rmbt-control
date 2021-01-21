package com.rtr.nettest.service;

import java.util.List;

public interface TestService {

    List<String> getDeviceHistory(Long clientId);

    List<String> getGroupNameByClientId(Long clientId);

}
