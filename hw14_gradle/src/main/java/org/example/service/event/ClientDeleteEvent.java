package org.example.service.event;

import lombok.Data;

@Data
public class ClientDeleteEvent {
    private final Long clientId;
}