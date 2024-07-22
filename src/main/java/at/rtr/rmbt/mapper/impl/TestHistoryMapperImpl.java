package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestHistoryMapper;
import at.rtr.rmbt.model.TestHistory;
import at.rtr.rmbt.response.HistoryItemResponse;
import at.rtr.rmbt.utils.ClassificationUtils;
import at.rtr.rmbt.utils.FormatUtils;
import at.rtr.rmbt.utils.MeasurementUtils;
import at.rtr.rmbt.utils.TimeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

@Service
public class TestHistoryMapperImpl implements TestHistoryMapper {
    @Override
    public HistoryItemResponse testHistoryToHistoryItemResponse(TestHistory testHistory, Integer classificationCount, Locale locale, boolean includeFailedTests) {
        HistoryItemResponse.HistoryItemResponseBuilder historyItemResponseBuilder = HistoryItemResponse.builder()
                .testUUID(testHistory.getUuid())
                .openTestUuid(testHistory.getOpenTestUuid() != null ? "O" + testHistory.getOpenTestUuid() : null)
                .time(testHistory.getTime().getTime())
                .timezone(testHistory.getTimezone())
                .timeString(TimeUtils.getTimeString(testHistory.getTime(), TimeZone.getTimeZone(testHistory.getTimezone()), locale))
                .speedDownload(FormatUtils.formatSpeed(testHistory.getSpeedDownload()))
                .speedUpload(FormatUtils.formatSpeed(testHistory.getSpeedUpload()))
                .ping(FormatUtils.formatPing(testHistory.getPingMedian()))
                .pingShortest(FormatUtils.formatPing(testHistory.getPingMedian()))
                .model(testHistory.getModel())
                .networkType(testHistory.getNetworkTypeGroupName())
                .loopUUID(FormatUtils.formatLoopUUID(testHistory.getLoopUuid()))
                .speedUploadClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_UPLOAD, ObjectUtils.defaultIfNull(testHistory.getSpeedUpload(), NumberUtils.INTEGER_ZERO), classificationCount))
                .speedDownloadClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_DOWNLOAD, ObjectUtils.defaultIfNull(testHistory.getSpeedDownload(), NumberUtils.INTEGER_ZERO), classificationCount))
                .pingClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_PING, ObjectUtils.defaultIfNull(testHistory.getPingMedian(), NumberUtils.LONG_ZERO), classificationCount))
                .pingShortestClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_PING, ObjectUtils.defaultIfNull(testHistory.getPingMedian(), NumberUtils.LONG_ZERO), classificationCount));
        if (includeFailedTests) {
            historyItemResponseBuilder.status(testHistory.getStatus().toLowerCase());
        }
        setSignalFields(testHistory, historyItemResponseBuilder, classificationCount);
        return historyItemResponseBuilder.build();
    }

    private void setSignalFields(TestHistory testHistory, HistoryItemResponse.HistoryItemResponseBuilder historyItemResponseBuilder, Integer classificationCount) {
        boolean dualSim = MeasurementUtils.isDualSim(testHistory.getNetworkType(), testHistory.getDualSim());
        boolean useSignal = MeasurementUtils.isUseSignal(testHistory.getSimCount(), dualSim);
        if (useSignal) {
            if (Objects.nonNull(testHistory.getSignalStrength())) {
                int[] threshold = ClassificationUtils.getThresholdForSignal(testHistory.getNetworkType());
                historyItemResponseBuilder
                        .signalStrength(testHistory.getSignalStrength())
                        .signalClassification(ClassificationUtils.classify(threshold, ObjectUtils.defaultIfNull(testHistory.getSignalStrength(), NumberUtils.INTEGER_ZERO), classificationCount));
            }
            if (Objects.nonNull(testHistory.getLteRsrp())) {
                historyItemResponseBuilder
                        .lteRSRP(testHistory.getLteRsrp())
                        .signalClassification(ClassificationUtils.classify(ClassificationUtils.THRESHOLD_SIGNAL_RSRP, ObjectUtils.defaultIfNull(testHistory.getLteRsrp(), NumberUtils.INTEGER_ZERO), classificationCount));
            }
        }
    }
}
