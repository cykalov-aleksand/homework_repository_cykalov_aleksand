package org.example.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Table(name = "client")
public class Client {
    @Id
    private Long id;
    private String name;

    @MappedCollection(idColumn = "client_id", keyColumn = "position")
    private List<Phone> phones = new ArrayList<>();

    @MappedCollection(idColumn = "client_id")
    private Address address;
}

