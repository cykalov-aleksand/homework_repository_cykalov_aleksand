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
    public ClientTableDto toDto(Client client, Address address, List<Phone> phones) {
        ClientTableDto dto = new ClientTableDto();
        dto.setId(client.getId());
        dto.setName(client.getName());

        dto.setAddressStreet(address != null ? address.getStreet() : "—");

        List<String> numbers = new ArrayList<>();
        if (phones != null) {
            for (Phone p : phones) {
                if (p != null && p.getNumber() != null && !p.getNumber().isBlank()) {
                    numbers.add(p.getNumber());
                }
            }
        }
        dto.setPhoneNumbers(numbers);

        return dto;
    }
}

