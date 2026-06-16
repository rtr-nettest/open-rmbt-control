package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestMapper;
import at.rtr.rmbt.model.Test;
import at.rtr.rmbt.request.SignalMeasurementResultRequest;
import at.rtr.rmbt.request.ResultRequest;
import at.rtr.rmbt.response.TestResponse;
import at.rtr.rmbt.utils.GeometryUtils;
import org.apache.commons.lang3.StringUtils;
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
    public void updateTestWithResultRequest(ResultRequest resultRequest, Test test) {
        test.setClientVersion(StringUtils.left(resultRequest.getClientVersion(), 10));
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
        test.setCertMode(resultRequest.getCertMode());
        test.setApn(resultRequest.getTelephonyAPN());
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
    public void updateTestWithSignalMeasurementResultRequest(SignalMeasurementResultRequest signalMeasurementResultRequest, Test test) {
        test.setClientVersion(StringUtils.left(signalMeasurementResultRequest.getClientVersion(), 10));
        test.setClientLanguage(signalMeasurementResultRequest.getClientLanguage());
        test.setPlatform(signalMeasurementResultRequest.getPlatform());
        test.setOsVersion(signalMeasurementResultRequest.getOsVersion());
        test.setApiLevel(signalMeasurementResultRequest.getApiLevel());
        test.setDevice(signalMeasurementResultRequest.getDevice());
        test.setModel(signalMeasurementResultRequest.getModel());
        test.setProduct(signalMeasurementResultRequest.getProduct());
        test.setPhoneType(signalMeasurementResultRequest.getTelephonyPhoneType());
        test.setDataState(signalMeasurementResultRequest.getTelephonyDataState());
        test.setNetworkCountry(signalMeasurementResultRequest.getTelephonyNetworkCountry());
        test.setNetworkOperatorName(signalMeasurementResultRequest.getTelephonyNetworkOperatorName());
        test.setNetworkSimCountry(signalMeasurementResultRequest.getTelephonyNetworkSimCountry());
        test.setNetworkSimOperatorName(signalMeasurementResultRequest.getTelephonyNetworkSimOperatorName());
        test.setWifiSsid(signalMeasurementResultRequest.getWifiSSID());
        test.setWifiBssid(signalMeasurementResultRequest.getWifiBSSID());
        test.setWifiNetworkId(signalMeasurementResultRequest.getWifiNetworkId());
        test.setClientSoftwareVersion(signalMeasurementResultRequest.getClientSoftwareVersion());
        test.setNetworkType(signalMeasurementResultRequest.getNetworkType());
        test.setNetworkIsRoaming(signalMeasurementResultRequest.getTelephonyNetworkIsRoaming());
        test.setTestErrorCause(signalMeasurementResultRequest.getTestErrorCause());
        // Optional termination cause; null/empty stays as-is, longer values are quietly
        // truncated to the column's 100 char limit.
        test.setTerminationCause(StringUtils.left(signalMeasurementResultRequest.getTerminationCause(), 100));
        test.setNetworkOperator(signalMeasurementResultRequest.getTelephonyNetworkOperator());
        test.setNetworkSimOperator(signalMeasurementResultRequest.getTelephonyNetworkSimOperator());
        test.setClientIpLocal(signalMeasurementResultRequest.getTestIpLocal());
        test.setTemperature(signalMeasurementResultRequest.getTemperature());
        test.setTimezone(signalMeasurementResultRequest.getTimezone());
        test.setApn(signalMeasurementResultRequest.getTelephonyAPN());
        test.setSubmissionRetryCount(
                signalMeasurementResultRequest.getSubmissionRetryCount() == null
                        ? null
                        : Math.toIntExact(signalMeasurementResultRequest.getSubmissionRetryCount())
        );



}
}
