package org.example.repository;

import org.example.model.Address;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends CrudRepository<Address, Long> {
    Optional<Address> findByClientId(long clientId);

    List<Address> findAllByClientIdIn(List<Long> clientIds);
}

