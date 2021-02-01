package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.TestServerMapper;
import at.rtr.rmbt.model.TestServer;
import at.rtr.rmbt.request.TestServerRequest;
import at.rtr.rmbt.response.TestServerResponse;
import at.rtr.rmbt.response.TestServerResponseForSettings;
import at.rtr.rmbt.service.impl.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServerMapperImpl implements TestServerMapper {
    private final UUIDGenerator uuidGenerator;

    @Override
    public TestServerResponseForSettings testServerToTestServerResponseForSettings(TestServer testServer) {
        return TestServerResponseForSettings.builder()
                .uuid(testServer.getUuid().toString())
                .name(testServer.getName())
                .build();
    }

    @Override
    public TestServerResponse testServerToTestServerResponse(TestServer testServer) {
        return TestServerResponse.builder()
                .uid(testServer.getUid())
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
                .serverType(testServer.getServerType())
                .priority(testServer.getPriority())
                .weight(testServer.getWeight())
                .active(testServer.getActive())
                .key(testServer.getKey())
                .selectable(testServer.getSelectable())
                .node(testServer.getNode())
                .build();
    }

    @Override
    public TestServer testServerRequestToTestServer(TestServerRequest testServerRequest) {
        return TestServer.builder()
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
                .serverType(testServerRequest.getServerType())
                .priority(testServerRequest.getPriority())
                .uuid(uuidGenerator.generateUUID())
                .weight(testServerRequest.getWeight())
                .active(testServerRequest.getActive())
                .key(testServerRequest.getKey())
                .selectable(testServerRequest.getSelectable())
                .node(testServerRequest.getNode())
                .build();
    }
}
