package org.example.cachehw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MyCacheTest {

    @Test
    @DisplayName("put и get должны сохранять и возвращать значения по ключу")
    void shouldPutAndGet() {
        var cache = new MyCache<String, Integer>();

        cache.put("one", 1);
        cache.put("two", 2);

        assertThat(cache.get("one")).isEqualTo(1);
        assertThat(cache.get("two")).isEqualTo(2);
    }

    @Test
    @DisplayName("get для несуществующего ключа должен вернуть null")
    void shouldReturnNullForMissingKey() {
        var cache = new MyCache<String, Integer>();

        assertThat(cache.get("missing")).isNull();
    }

    @Test
    @DisplayName("remove должен удалять значение по ключу")
    void shouldRemoveValueByKey() {
        var cache = new MyCache<String, Integer>();
        cache.put("key", 42);

        cache.remove("key");

        assertThat(cache.get("key")).isNull();
    }

    @Test
    @DisplayName("put с существующим ключом должен перезаписывать значение")
    void shouldOverwriteExistingKey() {
        var cache = new MyCache<String, String>();

        cache.put("key", "old");
        cache.put("key", "new");

        assertThat(cache.get("key")).isEqualTo("new");
    }

    @Test
    @DisplayName("remove для несуществующего ключа не должен выбрасывать исключение")
    void shouldNotThrowWhenRemovingMissingKey() {
        var cache = new MyCache<String, Integer>();

        org.assertj.core.api.Assertions.assertThatCode(() -> cache.remove("ghost"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addListener и notify: слушатель должен получать уведомления при put, get, remove")
    void shouldNotifyListenerOnAllActions() {
        var cache = new MyCache<String, Integer>();
        var actions = new ArrayList<String>();

        HwListener<String, Integer> listener = (key, value, action) -> actions.add(action);
        cache.addListener(listener);

        cache.put("key", 1);
        cache.get("key");
        cache.remove("key");

        assertThat(actions).containsExactly("put", "get", "remove");
    }

    @Test
    @DisplayName("removeListener должен отменять уведомления")
    void shouldStopNotifyingAfterRemoveListener() {
        var cache = new MyCache<String, Integer>();
        var actions = new ArrayList<String>();

        HwListener<String, Integer> listener = (key, value, action) -> actions.add(action);
        cache.addListener(listener);

        cache.put("a", 1);
        cache.removeListener(listener);
        cache.put("b", 2);

        assertThat(actions).containsExactly("put");
    }

    @Test
    @DisplayName("addListener(null) не должен приводить к ошибкам")
    void shouldIgnoreNullListener() {
        var cache = new MyCache<String, Integer>();

        org.assertj.core.api.Assertions.assertThatCode(() -> cache.addListener(null))
                .doesNotThrowAnyException();

        cache.put("key", 1);
        assertThat(cache.get("key")).isEqualTo(1);
    }

    @Test
    @DisplayName("несколько слушателей должны получать уведомления независимо")
    void shouldNotifyMultipleListeners() {
        var cache = new MyCache<String, Integer>();
        var firstLog = new ArrayList<String>();
        var secondLog = new ArrayList<String>();

        cache.addListener((k, v, a) -> firstLog.add(a));
        cache.addListener((k, v, a) -> secondLog.add(a));

        cache.put("x", 10);

        assertThat(firstLog).containsExactly("put");
        assertThat(secondLog).containsExactly("put");
    }
    @Test
    @DisplayName("concurrent access: put/get/remove должны работать без ошибок при корректной синхронизации")
    void testConcurrentPutGetRemove() throws InterruptedException {
        var cache = new MyCache<String, Integer>();
        var errors = new AtomicInteger(0);

        Runnable worker = () -> {
            // Уникальный префикс для каждого потока — чтобы ключи не пересекались
            String prefix = "k" + Thread.currentThread().getId() + "_";
            var strongValues = new Integer[10];

            for (int i = 0; i < 1000; i++) {
                String key = prefix + (i % 10);
                Integer value = i;

                int idx = i % 10;
                strongValues[idx] = value;

                cache.put(key, value);
                Integer v = cache.get(key);

                if (v == null || !v.equals(value)) {
                    errors.incrementAndGet();
                }

                if (i % 300 == 0) {
                    cache.remove(key);
                }
            }
        };

        List<Thread> threads = IntStream.range(0, 8)
                .mapToObj(i -> new Thread(worker))
                .toList();

        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }

        assertEquals(0, errors.get(), "Не должно быть ошибок при конкурентном доступе");
    }
}
