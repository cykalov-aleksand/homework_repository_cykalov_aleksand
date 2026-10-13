package org.example.mapper;

import org.example.model.Address;
import org.example.model.Client;
import org.example.model.Phone;
import org.example.dto.ClientTableDto;
import org.example.repository.AddressRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClientMapper {
private final AddressRepository addressRepository;

    public ClientMapper(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Client toClient(ClientTableDto dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setName(dto.getName());

        // Гарантируем не-null список телефонов
        List<Phone> phones = new ArrayList<>();
        if (dto.getPhoneNumbers() != null) {
            for (String number : dto.getPhoneNumbers()) {
                if (number != null && !number.isBlank()) {
                    Phone phone = new Phone();
                    phone.setNumber(number);
                    phones.add(phone);
                }
            }
        }
        client.setPhones(phones);

        // addressId не ставим — он будет установлен в сервисе
        return client;
    }


    public ClientTableDto toDto(Client client) {
        ClientTableDto dto = new ClientTableDto();
        dto.setId(client.getId());
        dto.setName(client.getName());

        // Логика адреса (как раньше)
        if (client.getAddressId() != null) {
            Address address = addressRepository.findById(client.getAddressId()).orElse(null);
            dto.setAddressStreet(address != null ? address.getStreet() : "—");
        } else {
            dto.setAddressStreet("—");
        }

        // Теперь эта часть реально получит данные, потому что мы заполнили client.phones в сервисе
        List<String> numbers = new ArrayList<>();
        if (client.getPhones() != null) {
            for (Phone phone : client.getPhones()) {
                if (phone.getNumber() != null && !phone.getNumber().isBlank()) {
                    numbers.add(phone.getNumber());
                }
            }
        }
        dto.setPhoneNumbers(numbers);

        return dto;
    }
}

