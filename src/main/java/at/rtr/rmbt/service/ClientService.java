package at.rtr.rmbt.service;

import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncResponse;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    RtrClient getClientByUUID(UUID uuid);

    RtrClient saveClient(RtrClient rtrClient);

    SyncResponse sync(SyncRequest syncRequest);

    List<RtrClient> listSyncedClientsByClientUid(Long clientId);
}
