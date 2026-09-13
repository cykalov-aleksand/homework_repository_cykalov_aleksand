package org.example.crm.model;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "phone")
public class Phone {
    @Id
    @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_gen")
    private Long id;
    @ManyToOne
    @JoinColumn(name="client_id", nullable=false)
    private Client client;
    private String number;

    public Phone(Long id, String number) {
        this.id=id;
        this.number=number;
           }
    public Phone clone(Client newOwner) {
        Phone copy = new Phone(this.id, this.number);
        copy.setClient(newOwner);
        return copy;
    }
    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", client=" + (client != null ? client.getId() : null) +  // Только ID клиента
                '}';
    }
}
