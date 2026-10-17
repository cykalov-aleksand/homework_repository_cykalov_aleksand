package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ClientTableDto;
import org.example.exception.DuplicateClientNameException;
import org.example.mapper.ClientMapper;
import org.example.model.Address;
import org.example.model.Client;
import org.example.model.Phone;
import org.example.repository.AddressRepository;
import org.example.repository.ClientRepository;
import org.example.repository.PhoneRepository;
import org.springframework.stereotype.Service;
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

    @Transactional
    public ClientTableDto saveClient(ClientTableDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new DuplicateClientNameException("Имя клиента обязательно");
        }
        Client client = clientMapper.toEntity(dto);
        boolean isNew = client.getId() == null;

        if (isNew) {
           client= newClientMethod(dto);
        } else {
            client=updateClientMethod(dto);
        }
        return clientMapper.toDto(
                clientRepository.findById(client.getId())
                        .orElseThrow(() -> new DuplicateClientNameException("Клиент не найден"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ClientTableDto findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Клиент с ID=" + id + " не найден"));
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientTableDto> findAllForTable() {
        // 1. Получаем всех клиентов
        List<Client> clients = new ArrayList<>();
        clientRepository.findAll().forEach(clients::add);

        if (clients.isEmpty()) {
            return List.of();
        }

        List<Long> clientIds = clients.stream().map(Client::getId).toList();

        // 2. Загружаем все адреса одним запросом
        List<Address> allAddresses = addressRepository.findAllByClientIdIn(clientIds);
        Map<Long, Address> addressMap = allAddresses.stream()
                .collect(Collectors.toMap(Address::getClientId, a -> a));

        // 3. Загружаем все телефоны одним запросом
        List<Phone> allPhones = phoneRepository.findAllByClientIdIn(clientIds);
        Map<Long, List<Phone>> phoneMap = allPhones.stream()
                .collect(Collectors.groupingBy(Phone::getClientId));

        // 4. «Приклеиваем» данные к клиентам
        clients.forEach(client -> {
            client.setAddress(addressMap.get(client.getId()));
            client.setPhones(phoneMap.getOrDefault(client.getId(), List.of()));
        });

        // 5. Маппим в DTO
        return clients.stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
         }

    private Client newClientMethod(ClientTableDto dto) {
        Client client = clientMapper.toEntity(dto);
        // Проверка: есть ли клиент с таким именем
        Optional<Client> existingOpt = clientRepository.findByNameIgnoreCaseTrimmed(dto.getName());
        if (existingOpt.isPresent()) {
            throw new DuplicateClientNameException("Запись не произведена: клиент с именем '" + dto.getName() + "' уже существует");
        }

        // Сохраняем родителя, чтобы получить ID
        client = clientRepository.save(client);
        long clientId = client.getId();

        // Если в DTO был адрес — сохраняем его отдельно, проставив clientId
        if (client.getAddress() != null) {
            Address address = client.getAddress();
            address.setClientId(clientId);
            addressRepository.save(address);
        }
        // Телефоны: формируем список и сохраняем массово
        List<Phone> phones = new ArrayList<>();
        int position = 0;
        for (String number : dto.getPhoneNumbers()) {
            if (number != null && !number.isBlank()) {
                Phone phone = new Phone();
                phone.setNumber(number);
                phone.setPosition(position++);
                phone.setClientId(clientId);
                phones.add(phone);
            }
        }
        if (!phones.isEmpty()) {
            phoneRepository.saveAll(phones);
        }
        return client;
    }

    private Client updateClientMethod(ClientTableDto dto) {
        Client client = clientMapper.toEntity(dto);
        // Обновление существующего клиента
        long clientId = client.getId();
        String oldName = client.getName();

        client.setName(dto.getName());

        // Если имя изменилось — проверяем, не занято ли новое имя кем-то другим
        if (!oldName.trim().equalsIgnoreCase(dto.getName().trim())) {
            Optional<Client> existingOpt = clientRepository.findByNameIgnoreCaseTrimmed(dto.getName());
            if (existingOpt.isPresent() && !existingOpt.get().getId().equals(clientId)) {
                throw new RuntimeException("Запись не произведена: клиент с именем '" + dto.getName() + "' уже существует (другой клиент)");
            }
        }

        clientRepository.save(client); // обновляем родителя

        // Полная замена телефонов: удаляем старые, вставляем новые
        phoneRepository.deleteByClientId(clientId);

        List<Phone> phones = new ArrayList<>();
        int position = 0;
        for (String number : dto.getPhoneNumbers()) {
            if (number != null && !number.isBlank()) {
                Phone phone = new Phone();
                phone.setNumber(number);
                phone.setPosition(position++);
                phone.setClientId(clientId);
                phones.add(phone);
            }
        }
        if (!phones.isEmpty()) {
            phoneRepository.saveAll(phones);
        }

        // Логика адреса (One-to-One)
        Optional<Address> existingAddressOpt = addressRepository.findByClientId(clientId);
        if (dto.getAddressStreet() == null || dto.getAddressStreet().isBlank()) {
            existingAddressOpt.ifPresent(addressRepository::delete);
        } else if (existingAddressOpt.isPresent()) {
            Address existingAddress = existingAddressOpt.get();
            existingAddress.setStreet(dto.getAddressStreet());
            addressRepository.save(existingAddress);
        } else {
            Address newAddress = new Address();
            newAddress.setStreet(dto.getAddressStreet());
            newAddress.setClientId(clientId);
            addressRepository.save(newAddress);
        }
        return client;
    }
}