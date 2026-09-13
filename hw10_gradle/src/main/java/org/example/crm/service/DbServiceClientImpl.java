package org.example.crm.service;


import org.example.core.repository.DataTemplate;
import org.example.core.sesionmanager.TransactionManager;
import org.example.crm.model.Client;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var savedClient = client.getId() == null
                    ? clientDataTemplate.insert(session, client)
                    : clientDataTemplate.update(session, client);

            // Инициализируем связи
            Hibernate.initialize(savedClient.getPhones());
            if (savedClient.getAddress() != null) {
                Hibernate.initialize(savedClient.getAddress());
            }

            log.info("saved client: {}", savedClient);

            // Возвращаем клон без прокси
            return savedClient.clone();
        });
    }
    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var client = session.find(Client.class, id);
            if (client != null) {
                Hibernate.initialize(client.getPhones());
                if (client.getAddress() != null) {
                    Hibernate.initialize(client.getAddress());
                }
            }
            return Optional.ofNullable(client);
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> clientDataTemplate.findAll(session));
    }
}
