package at.rtr.rmbt.service.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.constant.Constants;
import at.rtr.rmbt.exception.TestNotFoundException;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.repository.TestRepository;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.service.TestService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestServiceImplTest {
    private TestService testService;

    @MockBean
    private TestRepository testRepository;
    @MockBean
    private TestMapper testMapper;

    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private TestResponse testResponse;

    @Before
    public void setUp() {
        testService = new TestServiceImpl(testRepository, testMapper);
    }

    @Test
    public void getDeviceHistory_whenCommonData_expectDevicesNameList() {
        var devices = Lists.newArrayList(TestConstants.DEFAULT_HISTORY_DEVICE);
        when(testRepository.getDistinctModelByClientId(TestConstants.DEFAULT_UID)).thenReturn(devices);

        var response = testService.getDeviceHistory(TestConstants.DEFAULT_UID);

        assertEquals(devices, response);
    }

    @Test
    public void getDeviceHistory_whenUnknownDevice_expectDevicesNameList() {
        List<String> devices = new ArrayList<>();
        devices.add(null);

        when(testRepository.getDistinctModelByClientId(TestConstants.DEFAULT_UID)).thenReturn(devices);

        var response = testService.getDeviceHistory(TestConstants.DEFAULT_UID);

        assertEquals(List.of(Constants.UNKNOWN_DEVICE), response);
    }

    @Test
    public void getGroupNameByClientId_whenCommonData_expectGroupNameList() {
        when(testRepository.getDistinctGroupNameByClientId(TestConstants.DEFAULT_UID)).thenReturn(List.of(TestConstants.DEFAULT_HISTORY_NETWORK));

        var response = testService.getGroupNameByClientId(TestConstants.DEFAULT_UID);

        assertEquals(List.of(TestConstants.DEFAULT_HISTORY_NETWORK), response);
    }

    @Test
    public void getTestByUUID_whenTestExist_expectTestResponse() {
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.of(test));
        when(testMapper.testToTestResponse(test)).thenReturn(testResponse);

        var response = testService.getTestByUUID(TestConstants.DEFAULT_TEST_UUID);

        assertEquals(testResponse, response);
    }

    @Test(expected = TestNotFoundException.class)
    public void getTestByUUID_whenTestNotExist_expectException() {
        when(testRepository.findByUuid(TestConstants.DEFAULT_TEST_UUID)).thenReturn(Optional.empty());
        testService.getTestByUUID(TestConstants.DEFAULT_TEST_UUID);
    }
}
