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

    public ClientTableDto toDto(Client client) {
        ClientTableDto dto = new ClientTableDto();
        dto.setId(client.getId());
        dto.setName(client.getName());

        // Адрес: используем подгруженный client.getAddress()
        Address address = client.getAddress();
        if (address != null) {
            dto.setAddressStreet(address.getStreet());
        } else {
            // Если адреса нет вообще (не должно быть при валидных данных), ставим заглушку
            dto.setAddressStreet("—");
        }

        // Телефоны: берём из client.getPhones(), который уже заполнен в сервисе
        List<String> numbers = new ArrayList<>();
        if (client.getPhones() != null) {
            for (Phone phone : client.getPhones()) {
                if (phone != null && phone.getNumber() != null && !phone.getNumber().isBlank()) {
                    numbers.add(phone.getNumber());
                }
            }
        }
        dto.setPhoneNumbers(numbers);

        return dto;
    }
}

