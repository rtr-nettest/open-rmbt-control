package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.TestHistory;
import at.rtr.rmbt.response.HistoryItemResponse;

import java.util.Locale;

public interface TestHistoryMapper {

    HistoryItemResponse testHistoryToHistoryItemResponse(TestHistory testHistory, Integer classificationCount, Locale locale, boolean includeFailedTests, boolean includeCoverageFences);
}
