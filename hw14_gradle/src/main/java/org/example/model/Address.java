package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@ToString
@Table(name = "address")
public class Address {
   @Id
    private Long id;
    private String street;
}


