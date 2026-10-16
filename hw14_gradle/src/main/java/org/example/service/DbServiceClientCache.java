package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ClientTableDto;
import org.example.service.cachehw.MyCache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbServiceClientCache implements DbServiceClient {
    private final DbService dbService;
    private final MyCache<Long, ClientTableDto> cache;

    @Override
    public ClientTableDto saveClient(ClientTableDto clientTableDto) {
        ClientTableDto saved = dbService.saveClient(clientTableDto);
        if (saved.getId() != null) {
            cache.put(saved.getId(), saved);
        }
        return saved;
    }

    @Override
    public List<ClientTableDto> findAllForTable() {
        return dbService.findAllForTable();
    }

    @Override
    public ClientTableDto findById(Long id) {
        if (id == null) {
            return null;
        }
        ClientTableDto cached = cache.get(id);
        if (cached != null) {
            return cached;
        }
        ClientTableDto fromDb = dbService.findById(id);
        if (fromDb != null) {
            cache.put(id, fromDb);
        }

        return fromDb;
    }

    @Override
    public void deleteClient(Long id) {
        dbService.deleteClient(id);
        cache.remove(id);
    }
}
