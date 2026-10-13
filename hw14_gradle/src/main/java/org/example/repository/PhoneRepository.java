package org.example.repository;
import org.example.model.Phone;
import org.springframework.data.repository.CrudRepository;


import java.util.List;
public interface PhoneRepository extends CrudRepository<Phone, Long> {
        List<Phone> findAllByClientId(Long clientId);
}