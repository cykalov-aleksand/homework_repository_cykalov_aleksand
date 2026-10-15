package org.example.service.event;

import lombok.Data;
import org.example.dto.ClientTableDto;

@Data
public class ClientSaveEvent {
    private final Long clientId;
    private final ClientTableDto dto;
}
