package org.example.crm.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@ToString
@Entity
@Table(name = "address")
public class Address {
    @SequenceGenerator(name = "address_gen", sequenceName = "address_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
    @Id
    private Long id;
    private String street;

    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }

}
