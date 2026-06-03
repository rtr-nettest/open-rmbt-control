package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.TestHistory;
import at.rtr.rmbt.response.HistoryItemResponse;

import java.util.Locale;

/**
 * Test history mapper interface.
 */
public interface TestHistoryMapper {

    /**
     * Test history to history item response.
     *
     * @param testHistory the Test history
     * @param classificationCount the Classification count
     * @param locale the Locale
     * @param includeFailedTests the Include failed tests
     * @param includeCoverageFences the Include coverage fences
     * @return the result
     */
    HistoryItemResponse testHistoryToHistoryItemResponse(TestHistory testHistory, Integer classificationCount, Locale locale, boolean includeFailedTests, boolean includeCoverageFences);
}
