package org.example.service;

import org.example.dto.ClientTableDto;

import java.util.List;

public interface DbServiceClient {
    ClientTableDto saveClient(ClientTableDto clientTableDto);
     List<ClientTableDto> findAllForTable();
     ClientTableDto findById(Long id);
    void deleteClient(Long id);
}
