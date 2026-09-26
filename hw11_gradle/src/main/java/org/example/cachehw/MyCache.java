package org.example.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final WeakHashMap<K, V> cache = new WeakHashMap<>();
    private final List<HwListener<K, V>> listeners = new ArrayList<>();

    @Override
    public synchronized void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, "put");
    }

    @Override
    public synchronized void remove(K key) {
        V value = cache.remove(key);
        notifyListeners(key, value, "remove");
    }
    @Override
       public synchronized V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            notifyListeners(key, value, "get");
        }
        return value;
    }

    @Override
    public synchronized void addListener(HwListener<K, V> listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public synchronized void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        List<HwListener<K, V>> copy;
        synchronized (this) {
            copy = new ArrayList<>(listeners);
        }

        for (HwListener<K, V> listener : copy) {
            try {
                listener.notify(key, value, action);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
}
