package org.example.service.service_client;

import org.example.dto.ClientTableDto;

import java.util.List;

public interface DbServiceClient {
    ClientTableDto saveClient(ClientTableDto clientTableDto);
     List<ClientTableDto> findAllForTable();
      ClientTableDto getClientWithDetails(Long id);
    ClientTableDto findById(Long id);
    public void deleteClient(Long id);
}
