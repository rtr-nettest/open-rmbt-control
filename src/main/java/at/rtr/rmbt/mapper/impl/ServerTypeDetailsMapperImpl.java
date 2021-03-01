package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.ServerTypeDetailsMapper;
import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.request.ServerTypeDetailsRequest;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;
import org.springframework.stereotype.Service;

@Service
public class ServerTypeDetailsMapperImpl implements ServerTypeDetailsMapper {

    @Override
    public ServerTypeDetails serverTypeDetailRequestToServerTypeDetails(ServerTypeDetailsRequest serverTypeDetailsRequest) {
        return ServerTypeDetails.builder()
                .encrypted(serverTypeDetailsRequest.isEncrypted())
                .port(serverTypeDetailsRequest.getPort())
                .portSsl(serverTypeDetailsRequest.getPortSsl())
                .serverType(serverTypeDetailsRequest.getServerType())
                .build();
    }

    @Override
    public ServerTypeDetailsResponse serverTypeDetailsToServerTypeDetailsResponse(ServerTypeDetails serverTypeDetails) {
        return ServerTypeDetailsResponse.builder()
                .serverType(serverTypeDetails.getServerType())
                .encrypted(serverTypeDetails.isEncrypted())
                .port(serverTypeDetails.getPort())
                .portSsl(serverTypeDetails.getPortSsl())
                .build();
    }
}
