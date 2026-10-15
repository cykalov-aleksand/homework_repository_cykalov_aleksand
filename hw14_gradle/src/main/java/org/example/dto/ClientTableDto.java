package org.example.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class ClientTableDto {
    private Long id;
    private String name;
    private String addressStreet;
    private List<String> phoneNumbers;
    private Long addressId;
}