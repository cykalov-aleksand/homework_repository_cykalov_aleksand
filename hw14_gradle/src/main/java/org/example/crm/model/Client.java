package org.example.crm.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
@Data
@ToString
@Table("client")
public class Client {
    @Id
    private Long id;
    private String name;
   private Long addressId;
    @Transient
    private List<Phone> phones = new ArrayList<>();
    @Transient
    private Address address;
}

