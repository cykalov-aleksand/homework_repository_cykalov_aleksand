package org.example.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@ToString
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("number")
    List<Phone> phones=new ArrayList<>();

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = new ArrayList<>();
        if (phones != null) {
            phones.forEach(this::addPhone);
        }
    }
    public void addPhone(Phone phone) {
        if (phones == null) {
            phones = new ArrayList<>();
        }
        phones.add(phone);
        phone.setClient(this);
    }
    public void setAddress(Address address) {
        this.address = address;
        if (address != null) {
            address.setClient(this);
        }
    }
    public void removePhone(Phone phone) {
        phones.remove(phone);
        phone.setClient(null);
    }
    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Client clone = new Client(this.id, this.name);

        // Копируем address
        if (this.address != null) {
            Address addressCopy = new Address(this.address.getId(), this.address.getStreet());
            addressCopy.setClient(clone);
            clone.address = addressCopy;
        }

        // Копируем phones
        if (this.phones != null) {
            clone.phones = new ArrayList<>();
            for (Phone phone : this.phones) {
                Phone phoneCopy = new Phone(phone.getId(), phone.getNumber());
                phoneCopy.setClient(clone);
                clone.phones.add(phoneCopy);
            }
        }

        // Гарантируем, что address.client установлен
        if (clone.address != null) {
            clone.address.setClient(clone);
        }

        return clone;
    }
    }

