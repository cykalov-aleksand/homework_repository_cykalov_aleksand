package org.example.service.service_client_json;

import org.example.model.Client;

import java.util.List;

public interface DbServiceClientJson {
    Client saveClient(Client client);

    Client getClient(Long id);

    List<Client> findAll();

    Client findById(Long id);
}
