package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TestMapperImplTest {
    private TestMapper testMapper;

    @Mock
    private at.rtr.rmbt.model.Test test;
    @Mock
    private SignalResultRequest signalResultRequest;
    @Mock
    private ResultRequest resultRequest;

    @Before
    public void setUp() {
        testMapper = new TestMapperImpl();
    }

    @Test
    public void testToTestResponse_whenCommonData_expectTestResponse() {
        when(test.getTime()).thenReturn(TestConstants.DEFAULT_ZONED_DATE_TIME);
        when(test.getUuid()).thenReturn(TestConstants.DEFAULT_TEST_UUID);

        var response = testMapper.testToTestResponse(test);

        assertEquals(TestConstants.DEFAULT_TEST_UUID, response.getTestUUID());
        assertEquals(TestConstants.DEFAULT_ZONED_DATE_TIME, response.getTime());
    }

    @Test
    public void updateTestWithSignalResultRequest_whenCommonData_expectTestUpdated() {
        when(signalResultRequest.getClientVersion()).thenReturn(TestConstants.DEFAULT_CLIENT_VERSION);
        when(signalResultRequest.getClientLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(signalResultRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_TEST_PLATFORM);
        when(signalResultRequest.getOsVersion()).thenReturn(TestConstants.DEFAULT_OS_VERSION);
        when(signalResultRequest.getApiLevel()).thenReturn(TestConstants.DEFAULT_API_LEVEL);
        when(signalResultRequest.getDevice()).thenReturn(TestConstants.DEFAULT_DEVICE);
        when(signalResultRequest.getModel()).thenReturn(TestConstants.DEFAULT_MODEL);
        when(signalResultRequest.getProduct()).thenReturn(TestConstants.DEFAULT_PRODUCT);
        when(signalResultRequest.getTelephonyPhoneType()).thenReturn(TestConstants.DEFAULT_TELEPHONY_PHONE_TYPE);
        when(signalResultRequest.getTelephonyDataState()).thenReturn(TestConstants.DEFAULT_TELEPHONY_DATA_STATE);
        when(signalResultRequest.getTelephonyNetworkCountry()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_COUNTRY);
        when(signalResultRequest.getTelephonyNetworkOperatorName()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME);
        when(signalResultRequest.getTelephonyNetworkSimCountry()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_COUNTRY);
        when(signalResultRequest.getTelephonyNetworkSimOperatorName()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME);
        when(signalResultRequest.getWifiSSID()).thenReturn(TestConstants.DEFAULT_WIFI_SSID);
        when(signalResultRequest.getWifiBSSID()).thenReturn(TestConstants.DEFAULT_WIFI_BSSID);
        when(signalResultRequest.getWifiNetworkId()).thenReturn(TestConstants.DEFAULT_WIFI_NETWORK_ID);
        when(signalResultRequest.getClientSoftwareVersion()).thenReturn(TestConstants.DEFAULT_CLIENT_SOFTWARE_VERSION);
        when(signalResultRequest.getNetworkType()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        when(signalResultRequest.getTelephonyNetworkIsRoaming()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_IS_ROAMING);
        when(signalResultRequest.getTestErrorCause()).thenReturn(TestConstants.DEFAULT_TEST_ERROR_CAUSE);

        testMapper.updateTestWithSignalResultRequest(signalResultRequest, test);

        verify(test).setClientVersion(TestConstants.DEFAULT_CLIENT_VERSION);
        verify(test).setClientLanguage(TestConstants.DEFAULT_LANGUAGE);
        verify(test).setPlatform(TestConstants.DEFAULT_TEST_PLATFORM);
        verify(test).setOsVersion(TestConstants.DEFAULT_OS_VERSION);
        verify(test).setApiLevel(TestConstants.DEFAULT_API_LEVEL);
        verify(test).setDevice(TestConstants.DEFAULT_DEVICE);
        verify(test).setModel(TestConstants.DEFAULT_MODEL);
        verify(test).setProduct(TestConstants.DEFAULT_PRODUCT);
        verify(test).setPhoneType(TestConstants.DEFAULT_TELEPHONY_PHONE_TYPE);
        verify(test).setDataState(TestConstants.DEFAULT_TELEPHONY_DATA_STATE);
        verify(test).setNetworkCountry(TestConstants.DEFAULT_TELEPHONY_NETWORK_COUNTRY);
        verify(test).setNetworkOperatorName(TestConstants.DEFAULT_TELEPHONY_NETWORK_OPERATOR_NAME);
        verify(test).setNetworkSimCountry(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_COUNTRY);
        verify(test).setNetworkSimOperatorName(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME);
        verify(test).setWifiSsid(TestConstants.DEFAULT_WIFI_SSID);
        verify(test).setWifiBssid(TestConstants.DEFAULT_WIFI_BSSID);
        verify(test).setWifiNetworkId(TestConstants.DEFAULT_WIFI_NETWORK_ID);
        verify(test).setClientSoftwareVersion(TestConstants.DEFAULT_CLIENT_SOFTWARE_VERSION);
        verify(test).setNetworkType(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        verify(test).setNetworkIsRoaming(TestConstants.DEFAULT_TELEPHONY_NETWORK_IS_ROAMING);
        verify(test).setTestErrorCause(TestConstants.DEFAULT_TEST_ERROR_CAUSE);
    }

    @Test
    public void updateTestWithResultRequest_whenCommonData_expectTestUpdated() {
        when(resultRequest.getClientVersion()).thenReturn(TestConstants.DEFAULT_CLIENT_VERSION);
        when(resultRequest.getClientName()).thenReturn(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        when(resultRequest.getClientLanguage()).thenReturn(TestConstants.DEFAULT_LANGUAGE);
        when(resultRequest.getUploadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED);
        when(resultRequest.getDownloadSpeed()).thenReturn(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        when(resultRequest.getPingShortest()).thenReturn(TestConstants.DEFAULT_RESULT_PING_SHORTEST);
        when(resultRequest.getPlatform()).thenReturn(TestConstants.DEFAULT_TEST_PLATFORM);
        when(resultRequest.getOsVersion()).thenReturn(TestConstants.DEFAULT_OS_VERSION);
        when(resultRequest.getApiLevel()).thenReturn(TestConstants.DEFAULT_API_LEVEL);
        when(resultRequest.getDevice()).thenReturn(TestConstants.DEFAULT_DEVICE);
        when(resultRequest.getModel()).thenReturn(TestConstants.DEFAULT_MODEL);
        when(resultRequest.getProduct()).thenReturn(TestConstants.DEFAULT_PRODUCT);
        when(resultRequest.getTelephonyPhoneType()).thenReturn(TestConstants.DEFAULT_TELEPHONY_PHONE_TYPE);
        when(resultRequest.getTelephonyDataState()).thenReturn(TestConstants.DEFAULT_TELEPHONY_DATA_STATE);
        when(resultRequest.getTelephonyNetworkCountry()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_COUNTRY);
        when(resultRequest.getTelephonyNetworkSimCountry()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_COUNTRY);
        when(resultRequest.getTelephonyNetworkSimOperatorName()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME);
        when(resultRequest.getWifiSSID()).thenReturn(TestConstants.DEFAULT_WIFI_SSID);
        when(resultRequest.getWifiBSSID()).thenReturn(TestConstants.DEFAULT_WIFI_BSSID);
        when(resultRequest.getWifiNetworkId()).thenReturn(TestConstants.DEFAULT_WIFI_NETWORK_ID);
        when(resultRequest.getTestNumThreads()).thenReturn(TestConstants.DEFAULT_TEST_NUM_THREADS);
        when(resultRequest.getTestBytesDownload()).thenReturn(TestConstants.DEFAULT_TEST_BYTES_DOWNLOAD);
        when(resultRequest.getTestBytesUpload()).thenReturn(TestConstants.DEFAULT_TEST_BYTES_UPLOAD);
        when(resultRequest.getDownloadDurationNanos()).thenReturn(TestConstants.DEFAULT_DOWNLOAD_DURATION_NANOS);
        when(resultRequest.getUploadDurationNanos()).thenReturn(TestConstants.DEFAULT_UPLOAD_DURATION_NANOS);
        when(resultRequest.getClientSoftwareVersion()).thenReturn(TestConstants.DEFAULT_CLIENT_SOFTWARE_VERSION);
        when(resultRequest.getNetworkType()).thenReturn(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        when(resultRequest.getTestTotalBytesDownload()).thenReturn(TestConstants.DEFAULT_TEST_TOTAL_BYTES_DOWNLOAD);
        when(resultRequest.getTestTotalBytesUpload()).thenReturn(TestConstants.DEFAULT_TEST_TOTAL_BYTES_UPLOAD);
        when(resultRequest.getTelephonyNetworkIsRoaming()).thenReturn(TestConstants.DEFAULT_TELEPHONY_NETWORK_IS_ROAMING);
        when(resultRequest.getTestIfBytesDownload()).thenReturn(TestConstants.DEFAULT_TEST_IF_BYTES_DOWNLOAD);
        when(resultRequest.getTestIfBytesUpload()).thenReturn(TestConstants.DEFAULT_TEST_IF_BYTES_UPLOAD);
        when(resultRequest.getTestdlIfBytesDownload()).thenReturn(TestConstants.DEFAULT_TEST_DL_IF_BYTES_DOWNLOAD);
        when(resultRequest.getTestdlIfBytesUploaded()).thenReturn(TestConstants.DEFAULT_TEST_DL_IF_BYTES_UPLOAD);
        when(resultRequest.getTestulIfBytesDownload()).thenReturn(TestConstants.DEFAULT_TEST_UL_IF_BYTES_DOWNLOAD);
        when(resultRequest.getTestulIfBytesUpload()).thenReturn(TestConstants.DEFAULT_TEST_UL_IF_BYTES_UPLOAD);
        when(resultRequest.getTimeDownloadOffsetNanos()).thenReturn(TestConstants.DEFAULT_TIME_DOWNLOAD_OFFSET_NANOS);
        when(resultRequest.getTimeUploadOffsetNanos()).thenReturn(TestConstants.DEFAULT_TIME_UPLOAD_OFFSET_NANOS);
        when(resultRequest.getTag()).thenReturn(TestConstants.DEFAULT_TAG);
        when(resultRequest.getUserServerSelection()).thenReturn(TestConstants.DEFAULT_USER_SERVER_SELECTION);
        when(resultRequest.getDualSim()).thenReturn(TestConstants.DEFAULT_DUAL_SIM);
        when(resultRequest.getTelephonySimCount()).thenReturn(TestConstants.DEFAULT_TELEPHONY_SIM_COUNT);
        when(resultRequest.getLastClientStatus()).thenReturn(TestConstants.DEFAULT_LAST_CLIENT_STATUS);
        when(resultRequest.getLastQosStatus()).thenReturn(TestConstants.DEFAULT_LAST_QOS_STATUS);
        when(resultRequest.getTestErrorCause()).thenReturn(TestConstants.DEFAULT_TEST_ERROR_CAUSE);
        when(resultRequest.getTestSubmissionRetryCount()).thenReturn(TestConstants.DEFAULT_TEST_SUBMISSION_RETRY_COUNT);


        testMapper.updateTestWithResultRequest(resultRequest, test);

        verify(test).setClientVersion(TestConstants.DEFAULT_CLIENT_VERSION);
        verify(test).setClientName(TestConstants.DEFAULT_TEST_SERVER_SERVER_TYPE);
        verify(test).setClientLanguage(TestConstants.DEFAULT_LANGUAGE);
        verify(test).setUploadSpeed(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED);
        verify(test).setDownloadSpeed(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED);
        verify(test).setShortestPing(TestConstants.DEFAULT_RESULT_PING_SHORTEST);
        verify(test).setPlatform(TestConstants.DEFAULT_TEST_PLATFORM);
        verify(test).setOsVersion(TestConstants.DEFAULT_OS_VERSION);
        verify(test).setApiLevel(TestConstants.DEFAULT_API_LEVEL);
        verify(test).setDevice(TestConstants.DEFAULT_DEVICE);
        verify(test).setModel(TestConstants.DEFAULT_MODEL);
        verify(test).setProduct(TestConstants.DEFAULT_PRODUCT);
        verify(test).setPhoneType(TestConstants.DEFAULT_TELEPHONY_PHONE_TYPE);
        verify(test).setDataState(TestConstants.DEFAULT_TELEPHONY_DATA_STATE);
        verify(test).setNetworkCountry(TestConstants.DEFAULT_TELEPHONY_NETWORK_COUNTRY);
        verify(test).setNetworkSimCountry(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_COUNTRY);
        verify(test).setNetworkSimOperatorName(TestConstants.DEFAULT_TELEPHONY_NETWORK_SIM_OPERATOR_NAME);
        verify(test).setWifiSsid(TestConstants.DEFAULT_WIFI_SSID);
        verify(test).setWifiBssid(TestConstants.DEFAULT_WIFI_BSSID);
        verify(test).setWifiNetworkId(TestConstants.DEFAULT_WIFI_NETWORK_ID);
        verify(test).setNumberOfThreads(TestConstants.DEFAULT_TEST_NUM_THREADS);
        verify(test).setBytesDownload(TestConstants.DEFAULT_TEST_BYTES_DOWNLOAD);
        verify(test).setBytesUpload(TestConstants.DEFAULT_TEST_BYTES_UPLOAD);
        verify(test).setNsecDownload(TestConstants.DEFAULT_DOWNLOAD_DURATION_NANOS);
        verify(test).setNsecUpload(TestConstants.DEFAULT_UPLOAD_DURATION_NANOS);
        verify(test).setClientSoftwareVersion(TestConstants.DEFAULT_CLIENT_SOFTWARE_VERSION);
        verify(test).setNetworkType(TestConstants.DEFAULT_NETWORK_TYPE_ID);
        verify(test).setTotalBytesDownload(TestConstants.DEFAULT_TEST_TOTAL_BYTES_DOWNLOAD);
        verify(test).setTotalBytesUpload(TestConstants.DEFAULT_TEST_TOTAL_BYTES_UPLOAD);
        verify(test).setNetworkIsRoaming(TestConstants.DEFAULT_TELEPHONY_NETWORK_IS_ROAMING);
        verify(test).setTestIfBytesDownload(TestConstants.DEFAULT_TEST_IF_BYTES_DOWNLOAD);
        verify(test).setTestIfBytesUpload(TestConstants.DEFAULT_TEST_IF_BYTES_UPLOAD);
        verify(test).setTestdlIfBytesDownload(TestConstants.DEFAULT_TEST_DL_IF_BYTES_DOWNLOAD);
        verify(test).setTestdlIfBytesUpload(TestConstants.DEFAULT_TEST_DL_IF_BYTES_UPLOAD);
        verify(test).setTestulIfBytesDownload(TestConstants.DEFAULT_TEST_UL_IF_BYTES_DOWNLOAD);
        verify(test).setTestulIfBytesUpload(TestConstants.DEFAULT_TEST_UL_IF_BYTES_UPLOAD);
        verify(test).setDownloadTimeNanoSeconds(TestConstants.DEFAULT_TIME_DOWNLOAD_OFFSET_NANOS);
        verify(test).setUploadTimeNanoSeconds(TestConstants.DEFAULT_TIME_UPLOAD_OFFSET_NANOS);
        verify(test).setTag(TestConstants.DEFAULT_TAG);
        verify(test).setUserServerSelection(TestConstants.DEFAULT_USER_SERVER_SELECTION);
        verify(test).setDualSim(TestConstants.DEFAULT_DUAL_SIM);
        verify(test).setSimCount(TestConstants.DEFAULT_TELEPHONY_SIM_COUNT);
        verify(test).setLastClientStatus(TestConstants.DEFAULT_LAST_CLIENT_STATUS);
        verify(test).setLastQosStatus(TestConstants.DEFAULT_LAST_QOS_STATUS);
        verify(test).setTestErrorCause(TestConstants.DEFAULT_TEST_ERROR_CAUSE);
        verify(test).setSubmissionRetryCount(TestConstants.DEFAULT_TEST_SUBMISSION_RETRY_COUNT);
    }
}
