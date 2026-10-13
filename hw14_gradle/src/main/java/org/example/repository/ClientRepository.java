package org.example.repository;

import org.example.model.Client;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client, Long> {
    @Query("SELECT c FROM Client c JOIN FETCH c.phones")
    List<Client> findAllWithPhones();

}
