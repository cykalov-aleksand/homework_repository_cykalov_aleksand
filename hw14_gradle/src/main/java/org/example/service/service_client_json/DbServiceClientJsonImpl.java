package org.example.service.service_client_json;

import lombok.RequiredArgsConstructor;
import org.example.model.Client;
import org.example.repository.AddressRepository;
import org.example.repository.ClientRepository;
import org.example.repository.PhoneRepository;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DbServiceClientJsonImpl implements DbServiceClientJson{
     private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    @Override
    public Client saveClient(Client client) {
        return null;
    }

    @Override
    public Client getClient(Long id) {
        return null;
    }

    @Override
    public List<Client> findAll() {
        return List.of();
    }

    @Override
    public Client findById(Long id) {
        return null;
    }
}
