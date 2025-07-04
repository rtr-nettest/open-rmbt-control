package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.CoverageResultRequest;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.request.SignalResultRequest;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TestMapperImpl implements TestMapper {

    @Override
    public TestResponse testToTestResponse(Test test) {
        return TestResponse.builder()
                .testUUID(test.getUuid())
                .time(test.getTime())
                .build();
    }

    @Override
    public void updateTestWithSignalResultRequest(SignalResultRequest signalResultRequest, Test test) {
        test.setClientVersion(signalResultRequest.getClientVersion());
        test.setClientLanguage(signalResultRequest.getClientLanguage());
        test.setPlatform(signalResultRequest.getPlatform());
        test.setOsVersion(signalResultRequest.getOsVersion());
        test.setApiLevel(signalResultRequest.getApiLevel());
        test.setDevice(signalResultRequest.getDevice());
        test.setModel(signalResultRequest.getModel());
        test.setProduct(signalResultRequest.getProduct());
        test.setPhoneType(signalResultRequest.getTelephonyPhoneType());
        test.setDataState(signalResultRequest.getTelephonyDataState());
        test.setNetworkCountry(signalResultRequest.getTelephonyNetworkCountry());
        test.setNetworkOperatorName(signalResultRequest.getTelephonyNetworkOperatorName());
        test.setNetworkSimCountry(signalResultRequest.getTelephonyNetworkSimCountry());
        test.setNetworkSimOperatorName(signalResultRequest.getTelephonyNetworkSimOperatorName());
        test.setWifiSsid(signalResultRequest.getWifiSSID());
        test.setWifiBssid(signalResultRequest.getWifiBSSID());
        test.setWifiNetworkId(signalResultRequest.getWifiNetworkId());
        test.setClientSoftwareVersion(signalResultRequest.getClientSoftwareVersion());
        test.setNetworkType(signalResultRequest.getNetworkType());
        test.setNetworkIsRoaming(signalResultRequest.getTelephonyNetworkIsRoaming());
        test.setTestErrorCause(signalResultRequest.getTestErrorCause());
    }

    @Override
    public void updateTestWithResultRequest(ResultRequest resultRequest, Test test) {
        test.setClientVersion(resultRequest.getClientVersion());
        test.setClientName(resultRequest.getClientName());
        test.setClientLanguage(resultRequest.getClientLanguage());
        test.setUploadSpeed(resultRequest.getUploadSpeed());
        test.setDownloadSpeed(resultRequest.getDownloadSpeed());
        test.setShortestPing(resultRequest.getPingShortest());
        test.setPlatform(resultRequest.getPlatform());
        test.setOsVersion(resultRequest.getOsVersion());
        test.setApiLevel(resultRequest.getApiLevel());
        test.setDevice(resultRequest.getDevice());
        test.setModel(resultRequest.getModel());
        test.setProduct(resultRequest.getProduct());
        test.setPhoneType(resultRequest.getTelephonyPhoneType());
        test.setDataState(resultRequest.getTelephonyDataState());
        test.setNetworkCountry(resultRequest.getTelephonyNetworkCountry());
        test.setNetworkOperatorName(resultRequest.getTelephonyNetworkOperatorName());
        test.setNetworkSimCountry(resultRequest.getTelephonyNetworkSimCountry());
        test.setNetworkSimOperatorName(resultRequest.getTelephonyNetworkSimOperatorName());
        test.setWifiSsid(resultRequest.getWifiSSID());
        test.setWifiBssid(resultRequest.getWifiBSSID());
        test.setWifiNetworkId(resultRequest.getWifiNetworkId());
        test.setNumberOfThreads(resultRequest.getTestNumThreads());
        test.setNumberOfThreadsUpload(resultRequest.getNumThreadsUl());
        test.setBytesDownload(resultRequest.getTestBytesDownload());
        test.setBytesUpload(resultRequest.getTestBytesUpload());
        test.setNsecDownload(resultRequest.getDownloadDurationNanos());
        test.setNsecUpload(resultRequest.getUploadDurationNanos());
        test.setClientSoftwareVersion(resultRequest.getClientSoftwareVersion());
        test.setNetworkType(resultRequest.getNetworkType());
        test.setTotalBytesDownload(resultRequest.getTestTotalBytesDownload());
        test.setTotalBytesUpload(resultRequest.getTestTotalBytesUpload());
        test.setNetworkIsRoaming(resultRequest.getTelephonyNetworkIsRoaming());
        test.setTestIfBytesDownload(resultRequest.getTestIfBytesDownload());
        test.setTestIfBytesUpload(resultRequest.getTestIfBytesUpload());
        test.setTestdlIfBytesDownload(resultRequest.getTestdlIfBytesDownload());
        test.setTestdlIfBytesUpload(resultRequest.getTestdlIfBytesUploaded());
        test.setTestulIfBytesDownload(resultRequest.getTestulIfBytesDownload());
        test.setTestulIfBytesUpload(resultRequest.getTestulIfBytesUpload());
        test.setDownloadTimeNanoSeconds(resultRequest.getTimeDownloadOffsetNanos());
        test.setUploadTimeNanoSeconds(resultRequest.getTimeUploadOffsetNanos());
        test.setTag(resultRequest.getTag());
        test.setUserServerSelection(resultRequest.getUserServerSelection());
        test.setDualSim(resultRequest.getDualSim());
        test.setSimCount(resultRequest.getTelephonySimCount());
        test.setLastClientStatus(resultRequest.getLastClientStatus());
        test.setLastQosStatus(resultRequest.getLastQosStatus());
        test.setTestErrorCause(resultRequest.getTestErrorCause());
        test.setSubmissionRetryCount(resultRequest.getTestSubmissionRetryCount());
    }

    @Override
    public Test updateTestLocation(Test test) {
        if (Objects.nonNull(test.getLongitude()) && Objects.nonNull(test.getLatitude())) {
            Geometry location = GeometryUtils.getPointEPSG900913FromLongitudeAndLatitude(test.getLongitude(), test.getLatitude());
            Geometry geom4326 = GeometryUtils.getPointEPSG4326FromLongitudeAndLatitude(test.getLongitude(), test.getLatitude());
            Geometry geom3857 = GeometryUtils.getPointEPSG3857FromLongitudeAndLatitude(test.getLongitude(), test.getLatitude());
            test.setLocation(location);
            test.setGeom4326(geom4326);
            test.setGeom3857(geom3857);
        }
        return test;
    }

    @Override
    public void updateTestWithCoverageResultRequest(CoverageResultRequest coverageResultRequest, Test test) {
        test.setClientVersion(coverageResultRequest.getClientVersion());
        test.setClientLanguage(coverageResultRequest.getClientLanguage());
        test.setPlatform(coverageResultRequest.getPlatform());
        test.setOsVersion(coverageResultRequest.getOsVersion());
        test.setApiLevel(coverageResultRequest.getApiLevel());
        test.setDevice(coverageResultRequest.getDevice());
        test.setModel(coverageResultRequest.getModel());
        test.setProduct(coverageResultRequest.getProduct());
        test.setPhoneType(coverageResultRequest.getTelephonyPhoneType());
        test.setDataState(coverageResultRequest.getTelephonyDataState());
        test.setNetworkCountry(coverageResultRequest.getTelephonyNetworkCountry());
        test.setNetworkOperatorName(coverageResultRequest.getTelephonyNetworkOperatorName());
        test.setNetworkSimCountry(coverageResultRequest.getTelephonyNetworkSimCountry());
        test.setNetworkSimOperatorName(coverageResultRequest.getTelephonyNetworkSimOperatorName());
        test.setWifiSsid(coverageResultRequest.getWifiSSID());
        test.setWifiBssid(coverageResultRequest.getWifiBSSID());
        test.setWifiNetworkId(coverageResultRequest.getWifiNetworkId());
        test.setClientSoftwareVersion(coverageResultRequest.getClientSoftwareVersion());
        test.setNetworkType(coverageResultRequest.getNetworkType());
        test.setNetworkIsRoaming(coverageResultRequest.getTelephonyNetworkIsRoaming());
        test.setTestErrorCause(coverageResultRequest.getTestErrorCause());

}
}
