package org.example.crm.repository;
import org.example.crm.model.Phone;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class PhoneRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhoneRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Phone> findByClientId(Long clientId) {
        return jdbcTemplate.query(
                "SELECT id, number, client_id FROM phone WHERE client_id = ?",
                (rs, rowNum) -> new Phone(
                        rs.getLong("id"),
                        rs.getLong("client_id"),
                        rs.getString("number")
                ),
                clientId
        );
    }

    public Phone save(Phone phone) {
        if (phone.getId() == null) {
            Long id = (long) jdbcTemplate.update(
                    "INSERT INTO phone (number, client_id) VALUES (?, ?)",
                    phone.getNumber(), phone.getClientId()
            );
            phone.setId(id); // если нужно получить ID после вставки
        } else {
            jdbcTemplate.update(
                    "UPDATE phone SET number = ?, client_id = ? WHERE id = ?",
                    phone.getNumber(), phone.getClientId(), phone.getId()
            );
        }
        return phone;
    }
}