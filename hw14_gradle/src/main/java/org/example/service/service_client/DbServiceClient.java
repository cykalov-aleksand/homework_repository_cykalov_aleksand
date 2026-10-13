package org.example.service.service_client;

import org.example.model.Client;
import org.example.dto.ClientTableDto;

import java.util.List;

public interface DbServiceClient {
    ClientTableDto saveClient(ClientTableDto clientTableDto);

    //Client getClient(Long id);
    List<ClientTableDto> findAllForTable();
  //  List<Client> findAll();
    ClientTableDto getClientWithDetails(Long id);
    ClientTableDto findById(Long id);
}
