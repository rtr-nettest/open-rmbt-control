package at.rtr.rmbt.mapper;

import at.rtr.rmbt.model.ServerTypeDetails;
import at.rtr.rmbt.request.ServerTypeDetailsRequest;
import at.rtr.rmbt.response.ServerTypeDetailsResponse;

public interface ServerTypeDetailsMapper {

    ServerTypeDetails serverTypeDetailRequestToServerTypeDetails(ServerTypeDetailsRequest serverTypeDetailsRequest);

    ServerTypeDetailsResponse serverTypeDetailsToServerTypeDetailsResponse(ServerTypeDetails serverTypeDetails);
}
