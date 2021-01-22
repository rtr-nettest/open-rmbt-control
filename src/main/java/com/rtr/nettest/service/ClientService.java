package com.rtr.nettest.service;

import com.rtr.nettest.model.RtrClient;

import java.util.UUID;

public interface ClientService {

    RtrClient getClientByUUID(UUID uuid);

    RtrClient saveClient(RtrClient rtrClient);
}
