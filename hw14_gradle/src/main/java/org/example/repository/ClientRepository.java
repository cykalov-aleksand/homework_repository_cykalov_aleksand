package org.example.repository;

import org.example.model.Client;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {
    @Query("SELECT * FROM client WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name))")
    Optional<Client> findByNameIgnoreCaseTrimmed(@Param("name") String name);
}
