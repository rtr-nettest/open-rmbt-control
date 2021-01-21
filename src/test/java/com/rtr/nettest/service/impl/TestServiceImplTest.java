package com.rtr.nettest.service.impl;

import com.rtr.nettest.constant.Constants;
import com.rtr.nettest.repository.TestRepository;
import com.rtr.nettest.service.TestService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.rtr.nettest.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServiceImplTest {
    private TestService testService;

    @MockBean
    private TestRepository testRepository;

    @Before
    public void setUp() {
        testService = new TestServiceImpl(testRepository);
    }

    @Test
    public void getDeviceHistory_whenCommonData_expectDevicesNameList() {
        var devices = Lists.newArrayList(DEFAULT_HISTORY_DEVICE);
        when(testRepository.getDistinctModelByClientId(DEFAULT_UID)).thenReturn(devices);

        var response = testService.getDeviceHistory(DEFAULT_UID);

        assertEquals(devices, response);
    }

    @Test
    public void getDeviceHistory_whenUnknownDevice_expectDevicesNameList() {
        List<String> devices = new ArrayList<>();
        devices.add(null);

        when(testRepository.getDistinctModelByClientId(DEFAULT_UID)).thenReturn(devices);

        var response = testService.getDeviceHistory(DEFAULT_UID);

        assertEquals(List.of(Constants.UNKNOWN_DEVICE), response);
    }

    @Test
    public void getGroupNameByClientId_whenCommonData_expectGroupNameList() {
        when(testRepository.getDistinctGroupNameByClientId(DEFAULT_UID)).thenReturn(List.of(DEFAULT_HISTORY_NETWORK));

        var response = testService.getGroupNameByClientId(DEFAULT_UID);

        assertEquals(List.of(DEFAULT_HISTORY_NETWORK), response);
    }
}