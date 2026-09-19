package org.example.service;

import org.example.base.AbstractHibernateTest;
import org.example.crm.model.Address;
import org.example.crm.model.Client;
import org.example.crm.model.Phone;
import org.hibernate.stat.EntityStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DbServiceClientCacheTest extends AbstractHibernateTest {

    @Test
    @DisplayName("повторный getClient должен брать данные из кэша, а не из БД")
    void shouldGetClientFromCacheOnSecondCall() {
               var client = new Client(
                null, "CacheTestClient",
                new Address(null, "TestStreet"),
                List.of(new Phone(null, "123-456")));

        var savedClient = dbServiceClient.saveClient(client);
        long id = savedClient.getId();

        // Сбрасываем статистику после сохранения
        sessionFactory.getStatistics().clear();

        // when — первый вызов getClient: должен пойти в БД (кэш пуст после saveClient? Нет, write-through положил)
        // На самом деле saveClient уже положил в кэш, так что первый getClient тоже возьмёт из кэша.
        var firstLoad = dbServiceClient.getClient(id).orElseThrow(() -> new AssertionError("Клиент не найден"));
        assertThat(firstLoad.getName()).isEqualTo("CacheTestClient");

        // Проверяем, что Hibernate НЕ загружал сущность из БД (всё из кэша)
        EntityStatistics stats = getUsageStatistics();
        assertThat(stats.getFetchCount())
                .as("Первый getClient не должен обращаться к БД, т.к. saveClient уже положил в кэш")
                .isEqualTo(0);

        // when — второй вызов getClient: точно из кэша
        var secondLoad = dbServiceClient.getClient(id).orElseThrow(() -> new AssertionError("Клиент не найден"));
        // then
             assertThat(stats.getFetchCount())
                .as("Второй getClient тоже не должен обращаться к БД")
                .isEqualTo(0);

        // Проверяем, что возвращаемые объекты независимы (клоны)
        assertThat(secondLoad).isNotSameAs(firstLoad);
    }

    @Test
    @DisplayName("saveClient обновляет кэш, и getClient возвращает свежие данные")
    void shouldUpdateCacheOnSave() {
        // given
        var client = new Client(
                null, "InitialName",
                new Address(null, "Street"),
                List.of(new Phone(null, "111-222")));
        var saved = dbServiceClient.saveClient(client);
        long id = saved.getId();

        // when — обновляем имя
        var toUpdate = dbServiceClient.getClient(id).orElseThrow();
        toUpdate.setName("UpdatedName");
        dbServiceClient.saveClient(toUpdate);

        // then — getClient должен вернуть обновлённое имя из кэша
        var loadedClient = dbServiceClient.getClient(id)
                .orElseThrow(() -> new AssertionError("Клиент не найден"));
        assertThat(loadedClient.getName()).isEqualTo("UpdatedName");
        // И при этом не было обращений к БД
        assertThat(getUsageStatistics().getFetchCount())
                .as("getClient после saveClient не должен обращаться к БД")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("клиент, отсутствующий в кэше, загружается из БД")
    void shouldLoadFromDbWhenNotInCache() {
        // given — сохраняем, но затем удаляем из кэша вручную
        var client = new Client(
                null, "DbLoadTest",
                new Address(null, "DbStreet"),
                List.of(new Phone(null, "999-999")));
        var saved = dbServiceClient.saveClient(client);
        long id = saved.getId();

        // Удаляем из кэша
        cache.remove(id);
        sessionFactory.getStatistics().clear();

        // when
        var loaded = dbServiceClient.getClient(id);

        // then — должен пойти в БД
        assertThat(loaded).isPresent();
        assertThat(getUsageStatistics().getFetchCount())
                .as("После удаления из кэша getClient должен обратиться к БД")
                .isGreaterThan(0);
    }
}
