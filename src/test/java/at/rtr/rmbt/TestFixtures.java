package at.rtr.rmbt;

import at.rtr.rmbt.enums.ClientType;
import at.rtr.rmbt.enums.NewsStatus;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.model.*;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.request.QosSendTestResultItem;
import at.rtr.rmbt.response.NewsListItemResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static at.rtr.rmbt.TestConstants.*;

public interface TestFixtures {
    ApplicationProperties applicationProperties = new ApplicationProperties(
        new ApplicationProperties.LanguageProperties(Set.of("en", "de"), "en"),
        Set.of("RMBT", "RMBTjs", "Open-RMBT", "RMBTws", "HW-PROBE"),
        "0.1.0 || 0.3.0 || ^1.0.0",
        1,
        2,
        3,
        10000,
        2000
    );

    at.rtr.rmbt.model.ClientType clientType = new at.rtr.rmbt.model.ClientType(10L, ClientType.DESKTOP);

    RtrClient client = new RtrClient(
        DEFAULT_UID,
        UUID.randomUUID(),
        clientType,
        ZONED_DATE_TIME,
        null,
        null,
        true,
        ZONED_DATE_TIME,
        false,
        1L,
        ZONED_DATE_TIME,
        ZONED_DATE_TIME
    );

    News newsEn = prepareNews()
        .textEn(DEFAULT_NEWS_TEXT_EN)
        .titleEn(DEFAULT_NEWS_TITLE_EN)
        .build();

    News newsDe = prepareNews()
        .textDe(DEFAULT_NEWS_TEXT_DE)
        .titleDe(DEFAULT_NEWS_TITLE_DE)
        .build();

    NewsRequest newsRequestEn = prepareNewsRequest()
        .text(DEFAULT_NEWS_TEXT_EN)
        .title(DEFAULT_NEWS_TITLE_EN)
        .language(LANGUAGE_EN)
        .build();

    NewsRequest newsRequestDe = prepareNewsRequest()
        .text(DEFAULT_NEWS_TEXT_DE)
        .title(DEFAULT_NEWS_TITLE_DE)
        .language(LANGUAGE_DE)
        .build();

    NewsView newsViewEn = NewsView.builder()
        .uid(DEFAULT_UID)
        .uuid(DEFAULT_UUID)
        .textEn(DEFAULT_NEWS_TEXT_EN)
        .titleEn(DEFAULT_NEWS_TITLE_EN)
        .status(NewsStatus.PUBLISHED)
        .platform(DEFAULT_PLATFORM)
        .minSoftwareVersionCode(DEFAULT_MIN_SOFTWARE_VERSION)
        .maxSoftwareVersionCode(DEFAULT_MAX_SOFTWARE_VERSION)
        .force(false)
        .active(true)
        .time(ZONED_DATE_TIME.toOffsetDateTime())
        .startsAt(ZONED_DATE_TIME)
        .endsAt(ZONED_DATE_TIME)
        .build();

    NewsListItemResponse newsListItemEn = NewsListItemResponse.builder()
        .uid(DEFAULT_UID)
        .uuid(DEFAULT_UUID)
        .content(DEFAULT_NEWS_TEXT_EN)
        .title(DEFAULT_NEWS_TITLE_EN)
        .language(LANGUAGE_EN)
        .status(NewsStatus.PUBLISHED)
        .android(true)
        .minSoftwareVersion(DEFAULT_MIN_SOFTWARE_VERSION)
        .maxSoftwareVersion(DEFAULT_MAX_SOFTWARE_VERSION)
        .active(true)
        .startDate(ZONED_DATE_TIME)
        .endDate(ZONED_DATE_TIME)
        .build();

    QosSendTestResultItem qosSendTestResultItem = QosSendTestResultItem.builder()
        .testType(TestType.DNS)
        .qosTestUid(TestConstants.DEFAULT_UID)
        .dnsResultDuration(1L)
        .dnsResultInfo("dnsResultInfo")
        .dnsObjectiveHost("dnsObjectiveHost")
        .dnsResultStatus("dnsResultStatus")
        .dnsObjectiveResolver("dnsObjectiveResolver")
        .dnsObjectiveTimeout(1L)
        .dnsResultEntriesFound("dnsResultEntriesFound")
        .dnsObjectiveDnsRecord("dnsObjectiveDnsRecord")
        .dnsResultEntries(List.of(new QosSendTestResultItem.DnsResultItem("dnsResultAddress", 1L)))
        .build();

    QosResultRequest qosResultRequest = QosResultRequest.builder()
        .testToken(DEFAULT_TEST_TOKEN+"_QOS")
        .clientLanguage(LANGUAGE_EN)
        .clientVersion(DEFAULT_CLIENT_VERSION)
        .clientName(DEFAULT_CLIENT_NAME)
        .androidClientUUID(DEFAULT_CLIENT_UUID.toString())
        .qosResults(List.of(qosSendTestResultItem))
        .build();


    QosTestObjective qosTestObjective = new QosTestObjective(
        qosSendTestResultItem.getQosTestUid(),
        TestType.DNS,
        1,
        null,
        null,
        1,
        "test desc",
        "test summary",
        null
    );

    QosTestResult qosTestResult = new QosTestResult(
        1L,
        DEFAULT_UID,
        qosTestObjective,
        1,
        0,
        false,
        false,
        "{\"operator\":null,\"on_failure\":null,\"on_success\":null,\"evaluate\":null,\"end_time_ns\":null,\"start_time_ns\":46962439926,\"duration_ns\":120941000,\"success_condition\":true,\"failure_type\":\"default\",\"success_type\":\"default\",\"on_failure_behaviour\":\"nothing\",\"on_success_behaviour\":\"nothing\",\"priority\":2147483647,\"tcp_result_in\":null,\"tcp_objective_in_port\":null,\"tcp_result_in_response\":null,\"tcp_result_out\":\"OK\",\"tcp_objective_out_port\":22,\"tcp_result_out_response\":\"PING\",\"tcp_objective_timeout\":5000000000}",
        "This is a test",
        "Test is ok",
        new HashMap<>()
    );

    private static News.NewsBuilder prepareNews() {
        return News.builder()
            .id(DEFAULT_UID)
            .uuid(DEFAULT_UUID)
            .platform(DEFAULT_PLATFORM)
            .minSoftwareVersionCode(DEFAULT_MIN_SOFTWARE_VERSION)
            .maxSoftwareVersionCode(DEFAULT_MAX_SOFTWARE_VERSION)
            .force(false)
            .active(true)
            .time(ZONED_DATE_TIME.toOffsetDateTime())
            .startsAt(ZONED_DATE_TIME)
            .endsAt(ZONED_DATE_TIME);
    }

    private static NewsRequest.NewsRequestBuilder prepareNewsRequest() {
        return NewsRequest.builder()
            .android(true)
            .androidMaxSoftwareVersion(DEFAULT_MAX_SOFTWARE_VERSION)
            .androidMinSoftwareVersion(DEFAULT_MIN_SOFTWARE_VERSION)
            .active(true)
            .force(false);
    }
}
