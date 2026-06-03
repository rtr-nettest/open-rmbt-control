package at.rtr.rmbt.service;

import at.rtr.rmbt.model.RtrClient;
import at.rtr.rmbt.request.SyncRequest;
import at.rtr.rmbt.response.SyncResponse;

import java.util.List;
import java.util.UUID;

/**
 * Client service interface.
 */
public interface ClientService {

    RtrClient getClientByUUID(UUID uuid);

    /**
     * Save client.
     *
     * @param rtrClient the Rtr client
     * @return the result
     */
    RtrClient saveClient(RtrClient rtrClient);

    /**
     * Sync.
     *
     * @param syncRequest the Sync request
     * @return the result
     */
    SyncResponse sync(SyncRequest syncRequest);

    /**
     * List synced client ids by client.
     *
     * @param client the Client
     * @return the result
     */
    List<Long> listSyncedClientIdsByClient(RtrClient client);
}
