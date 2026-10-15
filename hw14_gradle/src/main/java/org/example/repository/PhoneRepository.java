package org.example.repository;
import org.example.model.Phone;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;
public interface PhoneRepository extends CrudRepository<Phone, Long> {
        List<Phone> findAllByClientId(Long clientId);
        @Modifying
        @Query("DELETE FROM phone WHERE client_id = :clientId")
        void deleteByClientId(@Param("clientId") Long clientId);
        List<Phone> findAllByClientIdIn(List<Long> clientIds);
}