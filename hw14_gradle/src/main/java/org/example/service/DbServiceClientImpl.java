package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Address;
import org.example.model.Client;
import org.example.model.Phone;
import org.example.repository.AddressRepository;
import org.example.repository.ClientRepository;
import org.example.repository.PhoneRepository;
import org.example.dto.ClientTableDto;
import org.example.mapper.ClientMapper;
import org.example.service.cachehw.HwCache;
import org.example.service.event.ClientDeleteEvent;
import org.example.service.event.ClientSaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DbServiceClientImpl implements DbServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(DbServiceClientImpl.class);
    private final ApplicationEventPublisher eventPublisher;
    private final HwCache<Long, ClientTableDto> cache;
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

        phoneRepository.deleteByClientId(savedClientId);

        List<String> phoneNumbers = clientTableDto.getPhoneNumbers();
        if (phoneNumbers == null) {
            phoneNumbers = List.of();
        }

        for (String number : phoneNumbers) {
            if (number != null && !number.isBlank()) {
                Phone phone = new Phone();
                phone.setNumber(number);
                phone.setClientId(savedClientId);
                phoneRepository.save(phone);
            }
        }

        ClientTableDto clientDto = clientMapper.toDto(client);
        eventPublisher.publishEvent(new ClientSaveEvent(savedClientId, clientDto));
        return clientDto;
    }

    @Transactional(readOnly = true)
    @Override
    public ClientTableDto findById(Long id) {
        ClientTableDto dto = cache.get(id);
        if (dto != null) {
            return dto;
        }

        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return null;
        }

        // Подгружаем телефоны
        List<Phone> phones = phoneRepository.findAllByClientId(client.getId());
        client.setPhones(new ArrayList<>(phones));

        // Подгружаем адрес
        if (client.getAddressId() != null) {
            addressRepository.findById(client.getAddressId())
                    .ifPresent(client::setAddress);
        }

        dto = clientMapper.toDto(client); // вся сборка — только здесь
        cache.put(dto.getId(), dto);
        return dto;
    }
    @Transactional(readOnly = true)
    @Override
    public List<ClientTableDto> findAllForTable() {
        List<Client> clients = StreamSupport.stream(clientRepository.findAll().spliterator(), false).toList();
        if (clients.isEmpty()) {
            return List.of();
        }

        List<Long> clientIds = clients.stream().map(Client::getId).toList();

        List<Phone> phones = phoneRepository.findAllByClientIdIn(clientIds);
        Map<Long, List<Phone>> phonesByClientId = phones.stream()
                .collect(Collectors.groupingBy(Phone::getClientId));

        clients.forEach(client -> {
            client.setPhones(new ArrayList<>(phonesByClientId.getOrDefault(client.getId(), List.of())));
        });

        List<Long> neededAddressIds = clients.stream()
                .map(Client::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Address> addresses = List.of();
        if (!neededAddressIds.isEmpty()) {
            addresses = StreamSupport.stream(addressRepository.findAllById(neededAddressIds).spliterator(), false).toList();
        }

        Map<Long, Address> addressMap = addresses.stream()
                .collect(Collectors.toMap(
                        Address::getId,
                        Function.identity(),
                        (e, r) -> e));

        clients.forEach(client -> {
            if (client.getAddressId() != null) {
                Address addr = addressMap.get(client.getAddressId());
                if (addr != null) {
                    client.setAddress(addr);
                }
            }
        });

        // Маппим и сразу кладём в кэш
        List<ClientTableDto> resultDtos = clients.stream()
                .map(clientMapper::toDto)
                .toList();

        resultDtos.forEach(dto -> cache.put(dto.getId(), dto));
        return resultDtos;
    }
    @Transactional
    public void deleteClient(Long id) {
        // Публикуем событие: оно будет обработано только после успешного коммита
        eventPublisher.publishEvent(new ClientDeleteEvent(id));

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Клиент с ID " + id + " не найден!"));

        Long addressId = client.getAddressId();
        if (addressId == null) {
            throw new IllegalStateException("У клиента ID " + id + " отсутствует адрес — нарушено бизнес-правило");
        }

        // Удаляем адрес → благодаря ON DELETE CASCADE удаляются client и все phone
        addressRepository.deleteById(addressId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClientSaved(ClientSaveEvent event) {
        logger.debug("Кэш для clientId={} обновлён", event.getClientId());
        cache.put(event.getClientId(), event.getDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onClientDeleted(ClientDeleteEvent event) {
        logger.debug("Кэш для clientId={} очищен", event.getClientId());
        cache.remove(event.getClientId());
    }
}