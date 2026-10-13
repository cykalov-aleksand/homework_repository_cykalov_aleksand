package org.example.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Table("phone")
public class Phone {
    @Id
    private Long id;
    @Column("client_id")
    private Long clientId;
    private String number;

}

