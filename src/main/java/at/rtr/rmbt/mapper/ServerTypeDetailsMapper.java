package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.request.ServerTypeDetailsRequest;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;

/**
 * Server type details mapper interface.
 */
public interface ServerTypeDetailsMapper {

    /**
     * Server type detail request to server type details.
     *
     * @param serverTypeDetailsRequest the Server type details request
     * @return the result
     */
    ServerTypeDetails serverTypeDetailRequestToServerTypeDetails(ServerTypeDetailsRequest serverTypeDetailsRequest);

    /**
     * Server type details to server type details response.
     *
     * @param serverTypeDetails the Server type details
     * @return the result
     */
    ServerTypeDetailsResponse serverTypeDetailsToServerTypeDetailsResponse(ServerTypeDetails serverTypeDetails);
}
