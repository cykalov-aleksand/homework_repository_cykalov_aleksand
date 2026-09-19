package org.example.cachehw;

public interface HwListener<K, V> {
    void notify(K key, V value, String action);
}
