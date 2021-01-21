package com.rtr.nettest.service;

import com.rtr.nettest.model.Client;

import java.util.UUID;

public interface ClientService {

    Client getClientByUUID(UUID uuid);

    Client saveClient(Client client);
}
