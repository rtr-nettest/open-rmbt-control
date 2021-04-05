package at.rtr.rmbt;

import at.rtr.rmbt.enums.NewsStatus;
import at.rtr.rmbt.enums.TestType;
import at.rtr.rmbt.model.News;
import at.rtr.rmbt.model.NewsView;
import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.request.NewsRequest;
import at.rtr.rmbt.request.QosResultRequest;
import at.rtr.rmbt.request.QosSendTestResultItem;
import at.rtr.rmbt.response.NewsListItemResponse;

import java.util.List;
import java.util.Set;

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
