package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ClientTableDto;
import org.example.mapper.ClientMapper;
import org.example.model.Address;
import org.example.model.Client;
import org.example.model.Phone;
import org.example.repository.AddressRepository;
import org.example.repository.ClientRepository;
import org.example.repository.PhoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DbService implements DbServiceClient {
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    private final ClientMapper clientMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public ClientTableDto saveClient(ClientTableDto clientTableDto) {
        Long clientId = clientTableDto.getId();
        Client client;

        if (clientId != null) {
            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Клиент с ID " + clientId + " не найден!"));
        } else {
            client = new Client();
        }

        client.setName(clientTableDto.getName());

        // Работа с адресом
        Long addressId = clientTableDto.getAddressId();
        if (addressId != null) {
            Address existing = addressRepository.findById(addressId)
                    .orElseThrow(() -> new IllegalArgumentException("Адрес с ID " + addressId + " не найден!"));

            String newStreet = clientTableDto.getAddressStreet();
            if (newStreet != null && !newStreet.isBlank()
                    && !existing.getStreet().equals(newStreet)) {
                existing.setStreet(newStreet);
                addressRepository.save(existing);
            }
            client.setAddressId(existing.getId());
        } else {
            String street = (clientTableDto.getAddressStreet() != null && !clientTableDto.getAddressStreet().isBlank())
                    ? clientTableDto.getAddressStreet()
                    : "Адрес для " + (client.getName() != null ? client.getName() : "клиента");

            Address newAddress = new Address();
            newAddress.setStreet(street);
            Address savedAddress = addressRepository.save(newAddress);
            client.setAddressId(savedAddress.getId());
        }

        client = clientRepository.save(client);
        Long savedClientId = client.getId();

        // Телефоны: удаляем старые, добавляем новые
        phoneRepository.deleteByClientId(savedClientId);

        List<String> phoneNumbers = clientTableDto.getPhoneNumbers();
        if (phoneNumbers != null) {
            for (String number : phoneNumbers) {
                if (number != null && !number.isBlank()) {
                    Phone phone = new Phone();
                    phone.setNumber(number);
                    phone.setClientId(savedClientId);
                    phoneRepository.save(phone);
                }
            }
        }
        Address address = client.getAddressId() != null
                ? addressRepository.findById(client.getAddressId()).orElse(null)
                : null;
        List<Phone> phones = phoneRepository.findAllByClientId(savedClientId);

        Client stub = new Client();
        stub.setId(savedClientId);
        stub.setName(client.getName());

        return clientMapper.toDto(stub, address, phones);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientTableDto findById(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return null;
        }

        Address address = client.getAddressId() != null
                ? addressRepository.findById(client.getAddressId()).orElse(null)
                : null;
        List<Phone> phones = phoneRepository.findAllByClientId(client.getId());

        Client stub = new Client();
        stub.setId(client.getId());
        stub.setName(client.getName());

        return clientMapper.toDto(stub, address, phones);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientTableDto> findAllForTable() {
        Iterable<Client> clientsIterable = clientRepository.findAll();
        List<Client> clients = new ArrayList<>();
        clientsIterable.forEach(clients::add);

        if (clients.isEmpty()) {
            return List.of();
        }

        List<Long> clientIds = clients.stream().map(Client::getId).toList();

        // Один запрос на все телефоны
        List<Phone> phones = phoneRepository.findAllByClientIdIn(clientIds);
        Map<Long, List<Phone>> phonesByClientId = phones.stream()
                .collect(Collectors.groupingBy(Phone::getClientId));

        // Адреса: только для тех, у кого есть address_id
        List<Long> neededAddressIds = clients.stream()
                .map(Client::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, Address> addressMap = new HashMap<>();
        if (!neededAddressIds.isEmpty()) {
            Iterable<Address> addressesIterable = addressRepository.findAllById(neededAddressIds);
            for (Address a : addressesIterable) {
                addressMap.put(a.getId(), a);
            }
        }

        List<ClientTableDto> resultDtos = new ArrayList<>(clients.size());
        for (Client c : clients) {
            List<Phone> cPhones = phonesByClientId.getOrDefault(c.getId(), List.of());
            Address cAddress = c.getAddressId() != null ? addressMap.get(c.getAddressId()) : null;

            Client stub = new Client();
            stub.setId(c.getId());
            stub.setName(c.getName());

            resultDtos.add(clientMapper.toDto(stub, cAddress, cPhones));
        }

        return resultDtos;
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Клиент с ID " + id + " не найден!"));

        Long addressId = client.getAddressId();
        if (addressId == null) {
            throw new IllegalStateException("У клиента ID " + id + " отсутствует адрес — нарушено бизнес-правило");
        }

        // Удаляем адрес → так как в миграции используем ON DELETE CASCADE удалятся client и все phone
        addressRepository.deleteById(addressId);
    }
}
