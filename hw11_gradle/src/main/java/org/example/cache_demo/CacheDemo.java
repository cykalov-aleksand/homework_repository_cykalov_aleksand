package org.example.cache_demo;

import org.example.cachehw.HwCache;
import org.example.cachehw.MyCache;
import org.example.core.repository.DataTemplateHibernate;
import org.example.core.repository.HibernateUtils;
import org.example.core.sesionmanager.TransactionManagerHibernate;
import org.example.crm.dbmigrations.MigrationsExecutorFlyway;
import org.example.crm.model.Address;
import org.example.crm.model.Client;
import org.example.crm.model.Phone;
import org.example.service.DbServiceClientImpl;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CacheDemo {
    private static final Logger log = LoggerFactory.getLogger(CacheDemo.class);
    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(
                configuration, Client.class, Address.class, Phone.class);
        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        // === Демонстрация с кэшем ===
        HwCache<Long, Client> cache = new MyCache<>();
        var dbServiceWithCache = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        // === Демонстрация без кэша (пустой кэш, который ничего не хранит) ===
        HwCache<Long, Client> noOpCache = new NoOpCache<>();
        var dbServiceNoCache = new DbServiceClientImpl(transactionManager, clientTemplate, noOpCache);

        // Создаём тестового клиента
        var savedClient = dbServiceWithCache.saveClient(
                new Client(null, "CacheTestClient",
                        new Address(null, "TestStreet"),
                        List.of(new Phone(null, "13-555-22"))));
        long clientId = savedClient.getId();

        benchmark("БЕЗ кэша (каждый раз в БД)", dbServiceNoCache, clientId, 100);
        benchmark("С кэшем (cache-aside + write-through)", dbServiceWithCache, clientId, 100);

        log.info("=== Проверка сброса WeakHashMap при нехватке памяти ===");
        testWeakHashMapEviction();

        sessionFactory.close();
    }

    private static void benchmark(String label,
                                  DbServiceClientImpl dbService,
                                  long clientId,
                                  int iterations) {
        // Прогрев (1-й вызов всегда идёт в БД)
        dbService.getClient(clientId);

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            dbService.getClient(clientId);
        }
        long elapsed = System.nanoTime() - start;
        log.info("{}: {} вызовов за {} мс (в среднем {} нс/вызов)",
                label, iterations, elapsed / 1_000_000, elapsed / iterations);
    }

    // Показывает, что WeakHashMap освобождает записи,
    // когда на ключи нет сильных ссылок и GC собирает мусор.

    private static void testWeakHashMapEviction() {
        var cache = new MyCache<Long, byte[]>();

        // Кладём 1000 записей по 1 МБ.
        // Ключ — новый объект Long со значением 128+, т.к. Long.valueOf для значений
        // от -128 до 127 возвращает объекты из внутреннего кэша Long (сильная ссылка),
        // которые GC не сможет собрать. Значения 128+ создают новые объекты каждый раз.
        // После каждой итерации переменная key выходит из scope — сильной ссылки нет,
        // остаётся только слабая внутри WeakHashMap.
        // Памяти (128 МБ) не хватит на все 1000 МБ → GC собирает слабые ссылки,
        // а WeakHashMap при следующем put() освобождает устаревшие записи.
        for (int i = 0; i < 1000; i++) {
            Long key = Long.valueOf((long) i + 128);
            cache.put(key, new byte[1024 * 1024]); // 1 MB
        }

        log.info("Записи добавлены, запрашиваем GC");

        // Просим GC собрать мусор 5 раз с паузами,
        // чтобы дать JVM время фактически очистить слабые ссылки
        for (int i = 0; i < 5; i++) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Проверяем, сколько записей осталось в кэше после GC.
        // Если WeakHashMap работает корректно — все слабые ссылки собраны,
        // и remaining должен быть 0.
        int remaining = 0;
        for (long i = 128; i < 1128; i++) {
            if (cache.get(i) != null) {
                remaining++;
            }
        }
        log.info("Осталось записей в кэше после GC: {} из 1000", remaining);
        if (remaining < 1000) {
            log.info("✓ WeakHashMap корректно сбрасывается при нехватке памяти");
        } else {
            log.warn("Записи не сброшены — возможно, GC не успел отработать");
        }
    }

}