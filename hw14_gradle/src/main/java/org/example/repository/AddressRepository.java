package org.example.repository;


import org.example.model.Address;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends CrudRepository<Address, Long> {
    @Query("DELETE FROM address WHERE id = :id")
    void deleteByAddressId(@Param("id") Long id);
}


