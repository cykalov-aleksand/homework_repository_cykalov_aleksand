package org.example.mapper;

import org.example.model.Address;
import org.example.model.Client;
import org.example.model.Phone;
import org.example.dto.ClientTableDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ClientMapper {
    public Client toEntity(ClientTableDto dto) {
        if (dto == null) {
            return null;
        }

        Client client = new Client();
        client.setId(dto.getId());
        client.setName(dto.getName());

        // --- Адрес (один-к-одному) ---
        // Если в DTO есть улица, создаём Address. Если нет — оставляем null (связь не создаётся)
        if (dto.getAddressStreet() != null && !dto.getAddressStreet().isBlank()) {
            Address address = new Address();
            address.setStreet(dto.getAddressStreet());
            client.setAddress(address);
        } else {
            client.setAddress(null);
        }

        // --- Телефоны (один-ко-многим) ---
        List<Phone> phones = new ArrayList<>();
        if (dto.getPhoneNumbers() != null) {
            int position = 0;
            for (String number : dto.getPhoneNumbers()) {
                if (number != null && !number.isBlank()) {
                    Phone phone = new Phone();
                    phone.setNumber(number);
                    phone.setPosition(position++);
                    phones.add(phone);
                }
            }
        }
        client.setPhones(phones);

        return client;
    }

    public ClientTableDto toDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientTableDto dto = new ClientTableDto();
        dto.setId(client.getId());
        dto.setName(client.getName());
        Address address = client.getAddress();
        dto.setAddressStreet(address != null ? address.getStreet() : "—");

        // Телефоны: берём список из клиента и превращаем в List<String>
        List<String> phoneNumbers = new ArrayList<>();
        if (client.getPhones() != null) {
            for (Phone p : client.getPhones()) {
                if (p != null && p.getNumber() != null && !p.getNumber().isBlank()) {
                    phoneNumbers.add(p.getNumber());
                }
            }
        }
        dto.setPhoneNumbers(phoneNumbers);

        return dto;
    }
}

