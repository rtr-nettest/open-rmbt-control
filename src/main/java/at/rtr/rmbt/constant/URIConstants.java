package at.rtr.rmbt.constant;

public interface URIConstants {
    String NEWS_URL = "/news";
    String REGISTRATION_URL = "/testRequest";
    String RESULT_URL = "/result";
    String RESULT_QOS_URL = "/resultQoS";
    String SETTINGS_URL = "/settings";
    String ADMIN_NEWS = "/admin/news";
    String ADMIN_NEWS_ITEM = ADMIN_NEWS + "/{newsId}";
    String ADMIN_SETTING = "/admin/settings";
    String SIGNAL_REQUEST = "/signalRequest";
    String SIGNAL_RESULT = "/signalResult";
    String ADMIN_SIGNAL = "/reports/signal";
    String TEST_SERVER = "/measurementServer";
    String TEST = "/test";
    String BY_ID = "/{id}";
    String PROVIDERS = "/providers";
    String SIGNAL_STRENGTH_BY_UUID = "/signalStrength/{testUUID}";
    String BY_TEST_UUID = "/{testUUID}";
    String MEASUREMENT_QOS_REQUEST = "/qosTestRequest";
    String TEST_RESULT_DETAIL = "/testresultdetail";
    String REQUEST_DATA_COLLECTOR = "/requestDataCollector";
}
