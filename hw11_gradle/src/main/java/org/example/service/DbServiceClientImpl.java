package org.example.service;

import org.example.cachehw.HwCache;
import org.example.core.repository.DataTemplate;
import org.example.core.sesionmanager.TransactionManager;
import org.example.crm.model.Client;
import org.example.crm.service.DBServiceClient;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<Long, Client> cache;

    public DbServiceClientImpl(TransactionManager transactionManager,
                               DataTemplate<Client> clientDataTemplate,
                               HwCache<Long, Client> cache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            if (client.getPhones() != null) {
                client.getPhones().forEach(phone -> phone.setClient(client));
            }
            var savedClient = client.getId() == null
                    ? clientDataTemplate.insert(session, client)
                    : clientDataTemplate.update(session, client);

            Hibernate.initialize(savedClient.getPhones());
            if (savedClient.getAddress() != null) {
                Hibernate.initialize(savedClient.getAddress());
            }

            cache.put(savedClient.getId(), savedClient.clone());

            log.info("Сохраненный client: {}", savedClient);
            return savedClient.clone();
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        // Проверка наличия информации в кэше
        Client cached = cache.get(id);
        if (cached != null) {
            log.debug("Информация из кэша client  по id={}", id);
            return Optional.of(cached.clone());
        }

        log.debug("Кэш объекта client с id={} пуст, читаем информацию с DB", id);
        return transactionManager.doInReadOnlyTransaction(session -> {
            var client = session.find(Client.class, id);
            if (client != null) {
                Hibernate.initialize(client.getPhones());
                if (client.getAddress() != null) {
                    Hibernate.initialize(client.getAddress());
                }
                Client cloned = client.clone();
                cache.put(id, cloned);
                return Optional.of(cloned);
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(clientDataTemplate::findAll);
    }
}