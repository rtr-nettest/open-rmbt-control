package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.config.UUIDGenerator;
import at.rtr.rmbt.mapper.ServerTypeDetailsMapper;
import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServerMapperImpl implements TestServerMapper {
    private final UUIDGenerator uuidGenerator;
    private final ServerTypeDetailsMapper serverTypeDetailsMapper;

    @Override
    public TestServerResponseForSettings testServerToTestServerResponseForSettings(TestServer testServer) {
        return TestServerResponseForSettings.builder()
                .uuid(testServer.getUuid().toString())
                .name(testServer.getName())
                .build();
    }

    @Override
    public TestServerResponse testServerToTestServerResponse(TestServer testServer, Timestamp lastTestTimestamp, Timestamp lastSuccessfulTestTimestamp, boolean isLastMeasurementSuccess) {
        Set<ServerTypeDetailsResponse> serverTypeDetailsResponses = testServer.getServerTypeDetails().stream()
                .map(serverTypeDetailsMapper::serverTypeDetailsToServerTypeDetailsResponse)
                .collect(Collectors.toSet());

        return TestServerResponse.builder()
                .id(testServer.getUid())
                .uuid(testServer.getUuid())
                .name(testServer.getName())
                .webAddress(testServer.getWebAddress())
                .port(testServer.getPort())
                .portSsl(testServer.getPortSsl())
                .city(testServer.getCity())
                .country(testServer.getCountry())
                .latitude(testServer.getLatitude())
                .longitude(testServer.getLongitude())
                .location(testServer.getLocation())
                .webAddressIpV4(testServer.getWebAddressIpV4())
                .webAddressIpV6(testServer.getWebAddressIpV6())
                .serverTypeDetails(serverTypeDetailsResponses)
                .priority(testServer.getPriority())
                .weight(testServer.getWeight())
                .active(testServer.getActive())
                .secretKey(testServer.getKey())
                .selectable(testServer.getSelectable())
                .node(testServer.getNode())
                .timeOfLastMeasurement(lastTestTimestamp)
                .lastSuccessfulMeasurement(lastSuccessfulTestTimestamp)
                .lastMeasurementSuccess(isLastMeasurementSuccess)
                .build();
    }

    @Override
    public TestServer testServerRequestToTestServer(TestServerRequest testServerRequest) {
        Set<ServerTypeDetails> serverTypeDetails = getServerTypeDetails(testServerRequest);
        TestServer testServer = TestServer.builder()
                .name(testServerRequest.getName())
                .webAddress(testServerRequest.getWebAddress())
                .port(testServerRequest.getPort())
                .portSsl(testServerRequest.getPortSsl())
                .city(testServerRequest.getCity())
                .country(testServerRequest.getCountry())
                .latitude(testServerRequest.getLatitude())
                .longitude(testServerRequest.getLongitude())
                .location(testServerRequest.getLocation())
                .webAddressIpV4(testServerRequest.getWebAddressIpV4())
                .webAddressIpV6(testServerRequest.getWebAddressIpV6())
                .priority(testServerRequest.getPriority())
                .uuid(uuidGenerator.generateUUID())
                .weight(testServerRequest.getWeight())
                .active(testServerRequest.isActive())
                .key(testServerRequest.getSecretKey())
                .selectable(testServerRequest.isSelectable())
                .node(testServerRequest.getNode())
                .build();

        testServer.setServerTypeDetails(serverTypeDetails);
        return testServer;
    }

    private Set<ServerTypeDetails> getServerTypeDetails(TestServerRequest testServerRequest) {
        if (Objects.nonNull(testServerRequest.getServerTypeDetails())) {
            return testServerRequest.getServerTypeDetails().stream()
                    .map(serverTypeDetailsMapper::serverTypeDetailRequestToServerTypeDetails)
                    .collect(Collectors.toSet());
        } else {
            return null;
        }
    }
}
