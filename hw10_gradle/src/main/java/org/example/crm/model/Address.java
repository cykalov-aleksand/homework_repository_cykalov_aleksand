package org.example.crm.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @SequenceGenerator(name = "address_gen", sequenceName = "address_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
    @Id
    private Long id;
    private String street;
    @OneToOne(mappedBy = "address")
    private Client client;

    public Address(Long id, String street) {
        this.id=id;
        this.street=street;
    }
    public Address clone(Client newOwner) {
        Address copy = new Address(this.id, this.street);
        copy.setClient(newOwner);
        return copy;
    }
       @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", client=" + (client != null ? client.getId() : null) +  // Только ID
                '}';
    }
}
