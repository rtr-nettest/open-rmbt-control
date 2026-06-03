package at.rtr.rmbt.mapper.impl;

import at.rtr.rmbt.mapper.ServerTypeDetailsMapper;
import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.request.ServerTypeDetailsRequest;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;
import org.springframework.stereotype.Service;

/**
 * Server type details mapper impl class.
 */
@Service
public class ServerTypeDetailsMapperImpl implements ServerTypeDetailsMapper {

    /**
     * Server type detail request to server type details.
     *
     * @param serverTypeDetailsRequest the Server type details request
     * @return the result
     */
    @Override
    public ServerTypeDetails serverTypeDetailRequestToServerTypeDetails(ServerTypeDetailsRequest serverTypeDetailsRequest) {
        return ServerTypeDetails.builder()
                .encrypted(serverTypeDetailsRequest.isEncrypted())
                .port(serverTypeDetailsRequest.getPort())
                .portSsl(serverTypeDetailsRequest.getPortSsl())
                .serverType(serverTypeDetailsRequest.getServerType())
                .build();
    }

    /**
     * Server type details to server type details response.
     *
     * @param serverTypeDetails the Server type details
     * @return the result
     */
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
