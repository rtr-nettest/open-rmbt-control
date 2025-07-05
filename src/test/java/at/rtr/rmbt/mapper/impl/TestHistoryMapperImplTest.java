package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.TestConstants;
import at.rtr.rmbt.mapper.TestHistoryMapper;
import at.rtr.rmbt.model.TestHistory;
import at.rtr.rmbt.response.HistoryItemResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class TestHistoryMapperImplTest {
    private TestHistoryMapper testHistoryMapper;
    private TestHistory testHistory;
    private HistoryItemResponse.HistoryItemResponseBuilder expectedHistoryItemResponseBuilder;

    @Before
    public void setUp() {
        testHistoryMapper = new TestHistoryMapperImpl();
        testHistory = TestHistory.builder()
                .uuid(TestConstants.DEFAULT_TEST_UUID)
                .time(TestConstants.DEFAULT_DATE_TIME)
                .timezone(TestConstants.DEFAULT_TIMEZONE)
                .speedDownload(TestConstants.DEFAULT_RESULT_DOWNLOAD_SPEED)
                .speedUpload(TestConstants.DEFAULT_RESULT_UPLOAD_SPEED)
                .pingMedian(TestConstants.DEFAULT_TEST_PING_MEDIAN)
                .networkTypeGroupName(TestConstants.DEFAULT_TECHNOLOGY_FIRST.getLabelEn())
                .loopUuid(TestConstants.DEFAULT_LOOP_UUID)
                .model(TestConstants.DEFAULT_MODEL)
                .dualSim(TestConstants.DEFAULT_DUAL_SIM)
                .simCount(TestConstants.DEFAULT_TEST_SIM_COUNT)
                .build();
        expectedHistoryItemResponseBuilder = HistoryItemResponse.builder()
                .testUUID(TestConstants.DEFAULT_TEST_UUID)
                .time(TestConstants.DEFAULT_TIME_INSTANT)
                .timeString(TestConstants.DEFAULT_TEST_RESULT_DETAIL_TIME_STRING)
                .speedUpload(TestConstants.DEFAULT_FORMATTED_SPEED_UPLOAD)
                .speedDownload(TestConstants.DEFAULT_FORMATTED_SPEED_DOWNLOAD)
                .ping(TestConstants.DEFAULT_FORMATTED_PING)
                .pingShortest(TestConstants.DEFAULT_FORMATTED_PING_SHORTEST)
                .model(TestConstants.DEFAULT_MODEL)
                .networkType(TestConstants.DEFAULT_TECHNOLOGY_FIRST.getLabelEn())
                .loopUUID(TestConstants.DEFAULT_HISTORY_RESPONSE_ITEM_LOOP_UUID)
                .speedUploadClassification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .speedDownloadClassification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .pingClassification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .pingShortestClassification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .timezone(TestConstants.DEFAULT_TIMEZONE);
    }

    @Test
    public void testHistoryToHistoryItemResponse_whenSignalStrengthNotNull_expectHistoryItemResponse() {
        testHistory.setSignalStrength(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST);
        var expectedHistoryItemResponse = expectedHistoryItemResponseBuilder
                .signalStrength(TestConstants.DEFAULT_SIGNAL_STRENGTH_FIRST)
                .signalClassification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_SIGNAL_STRENGTH_CLASSIFICATION)
                .build();
        var response = testHistoryMapper.testHistoryToHistoryItemResponse(testHistory, TestConstants.DEFAULT_CLASSIFICATION_COUNT, Locale.ENGLISH, false,false);

        assertEquals(expectedHistoryItemResponse, response);
    }

    @Test
    public void testHistoryToHistoryItemResponse_whenLteRSPRNotNull_expectHistoryItemResponse() {
        testHistory.setLteRsrp(TestConstants.DEFAULT_LTE_RSRP_FIRST);
        var expectedHistoryItemResponse = expectedHistoryItemResponseBuilder
                .lteRSRP(TestConstants.DEFAULT_LTE_RSRP_FIRST)
                .signalClassification(TestConstants.DEFAULT_TEST_RESULT_MEASUREMENT_RESPONSE_CLASSIFICATION)
                .build();
        var response = testHistoryMapper.testHistoryToHistoryItemResponse(testHistory, TestConstants.DEFAULT_CLASSIFICATION_COUNT, Locale.ENGLISH, false,false);

        assertEquals(expectedHistoryItemResponse, response);
    }
}
