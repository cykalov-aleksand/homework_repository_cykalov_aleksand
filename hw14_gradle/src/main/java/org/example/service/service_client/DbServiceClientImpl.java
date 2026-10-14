package org.example.service.service_client;

import lombok.RequiredArgsConstructor;
import org.example.model.Address;
import org.example.model.Client;
import org.example.model.Phone;
import org.example.repository.AddressRepository;
import org.example.repository.ClientRepository;
import org.example.repository.PhoneRepository;
import org.example.dto.ClientTableDto;
import org.example.mapper.ClientMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DbServiceClientImpl implements DbServiceClient {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;
    private final ClientMapper clientMapper;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public ClientTableDto saveClient(ClientTableDto clientTableDto) {
        Long clientId = clientTableDto.getId();
        Client client;

        // ГЛАВНОЕ: если ID есть — загружаем существующую запись, иначе создаём новую
        if (clientId != null) {
            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Клиент с ID " + clientId + " не найден!"));
        } else {
            client = new Client();
        }

        // Обновляем поля
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
                addressRepository.save(existing); // В Spring Data JDBC нужно явно сохранить
            }
            client.setAddressId(existing.getId());
        } else {
            // Создаём новый адрес, если его нет
            String street = (clientTableDto.getAddressStreet() != null && !clientTableDto.getAddressStreet().isBlank())
                    ? clientTableDto.getAddressStreet()
                    : "Адрес для " + (client.getName() != null ? client.getName() : "клиента");

            Address newAddress = new Address();
            newAddress.setStreet(street);
            newAddress = addressRepository.save(newAddress);
            client.setAddressId(newAddress.getId());
        }

        // Сохраняем клиента: INSERT если id == null, UPDATE если id != null
        client = clientRepository.save(client);
        Long savedClientId = client.getId();

        // Синхронизация телефонов: удаляем старые, добавляем новые
        phoneRepository.deleteByClientId(savedClientId);

        List<String> phoneNumbers = parsePhoneString(clientTableDto.getPhoneNumbersStr());
        for (String number : phoneNumbers) {
            Phone phone = new Phone();
            phone.setNumber(number);
            phone.setClientId(savedClientId);
            phoneRepository.save(phone);
        }

        return clientMapper.toDto(client);
    }
    private List<String> parsePhoneString(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
    @Transactional
    @Override
    public ClientTableDto getClientWithDetails(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return null;
        }

        List<Phone> phones = new ArrayList<>();
        phoneRepository.findAllByClientId(client.getId()).forEach(phones::add);
        client.setPhones(phones);

        return clientMapper.toDto(client);
    }

    @Transactional
    @Override
    public ClientTableDto findById(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return null;
        }

        List<Phone> phones = new ArrayList<>();
        phoneRepository.findAllByClientId(client.getId()).forEach(phones::add);
        client.setPhones(phones);

        if (client.getAddressId() != null) {
            Optional<Address> addressOpt = addressRepository.findById(client.getAddressId());
            addressOpt.ifPresent(client::setAddress);
        }

        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
       @Override
    public List<ClientTableDto> findAllForTable() {
        // 1. Получаем всех клиентов (только их данные из таблицы client)
        List<Client> clients = StreamSupport.stream(clientRepository.findAll().spliterator(),false).toList();
       if (clients.isEmpty()) {
            return List.of();
        }

        // --- БЛОК ПОДГРУЗКИ ТЕЛЕФОНОВ ---
        // Получаем ВСЕ телефоны из БД одним запросом
        List<Phone> allPhones = StreamSupport.stream(phoneRepository.findAll().spliterator(), false)
                .toList();

        // Группируем их по client_id: Map<clientId, List<Phone>>
        Map<Long, List<Phone>> phonesByClientId = allPhones.stream()
                .collect(Collectors.groupingBy(Phone::getClientId));

        clients.forEach(client -> {
            // Если у клиента есть телефоны, кладем их в поле
            if (phonesByClientId.containsKey(client.getId())) {
                client.setPhones(phonesByClientId.get(client.getId()));
            }
        });
        // --------------------------------

        // --- БЛОК ПОДГРУЗКИ АДРЕСОВ ---
        // Сначала собираем все уникальные ID адресов, которые нужны
        List<Long> neededAddressIds = clients.stream()
                .map(Client::getAddressId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Если нужны адреса, получаем их из БД
        List<Address> addresses = new ArrayList<>();
        if (!neededAddressIds.isEmpty()) {
            addresses = StreamSupport.stream(addressRepository.findAllById(neededAddressIds).spliterator(),false).toList();
        }

        // Создаем быструю карту для поиска адреса по ID: Map<addressId, Address>
        Map<Long, Address> addressMap = addresses.stream()
                .collect(Collectors.toMap(Address::getId, Function.identity()));

        // Привязываем адреса к клиентам
        clients.forEach(client -> {
            if (client.getAddressId() != null) {
                Address addr = addressMap.get(client.getAddressId());
                if (addr != null) {
                    client.setAddress(addr); // Теперь в client.address будет реальный объект
                }
            }
        });
        // -------------------------------

        // 3. Конвертируем в DTO
        return clients.stream()
                .map(clientMapper::toDto)
                .toList();
    }
    @Transactional
    public void deleteClient(Long id){
        Long addressId = clientRepository.addressIdClient(id);
        clientRepository.deleteById(id);
        if (addressId != null) {
            addressRepository.deleteById(addressId);
        }
    }
}