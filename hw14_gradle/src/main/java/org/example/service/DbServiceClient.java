package org.example.service;

import org.example.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface DbServiceClient {
    Client saveClient(Client client);

    Client getClient(Long id);

    List<Client> findAll();
    Client getClientWithDetails(Long id);
    Client findById(Long id);
}
