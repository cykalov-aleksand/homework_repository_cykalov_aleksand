package org.example.crm.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@ToString()
@Table(name = "phone")
public class Phone {
    @Id
    @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_gen")
    private Long id;
    @ManyToOne
    @JoinColumn(name="client_id", nullable=false)
    @ToString.Exclude
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

}
