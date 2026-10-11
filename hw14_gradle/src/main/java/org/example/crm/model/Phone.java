package org.example.crm.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Table("phone")
public class Phone {
    @Id
    private Long id;
    private Long clientId;
    private String number;
public Phone(){}
    public Phone(Long id, Long clientId, String number) {
        this.id = id;
        this.clientId = clientId;
        this.number = number;
    }
}

