package org.example.dao;


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

public class DbServiceClientCache implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientCache.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<Long, Client> cache;

    public DbServiceClientCache(TransactionManager transactionManager,
                                DataTemplate<Client> clientDataTemplate,
                                HwCache<Long, Client> cache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        Client savedClientInTx = transactionManager.doInTransaction(session -> {
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
            log.info("Сохранённый client: {}", savedClient);
            // Возвращаем клон из транзакции, чтобы снаружи не было ссылки на Hibernate-объект
            return savedClient.clone();
        });

        // Кладём в кэш свою копию (чтобы кэш был изолирован)
        Client cacheEntry = savedClientInTx.clone();
        cache.put(cacheEntry.getId(), cacheEntry);

        // Возвращаем наружу ещё одну копию — тогда любые изменения снаружи не затронут кэш
        return savedClientInTx.clone();
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client cached = cache.get(id);
        if (cached != null) {
            log.debug("Информация из кэша client по id={}", id);
            // Всегда возвращаем отдельную копию: кэш остаётся неприкосновенным
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
                // Сначала делаем копию для кэша
                Client cacheEntry = client.clone();
                cache.put(id, cacheEntry);
                // Потом возвращаем наружу отдельную копию
                return Optional.of(client.clone());
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clients = clientDataTemplate.findAll(session);
            return clients.stream().map(client -> {
                Hibernate.initialize(client.getPhones());
                if (client.getAddress() != null) {
                    Hibernate.initialize(client.getAddress());
                }
                return client.clone();
            }).toList();
        });
    }
}