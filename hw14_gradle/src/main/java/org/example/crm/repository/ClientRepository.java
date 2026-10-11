package org.example.crm.repository;

import org.example.crm.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {

}
