package org.example.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@ToString
@Table(name = "phone")
public class Phone {
    @Id
    private Long id;
    private String number;
    private Integer position;

    @Column("client_id")  // <-- обязательно name =
    private Long clientId;
}

