package at.rtr.rmbt.service;

import at.rtr.rmbt.model.RtrClient;

import java.util.UUID;

public interface ClientService {

    RtrClient getClientByUUID(UUID uuid);

    RtrClient saveClient(RtrClient rtrClient);
}
