package io.github.liana.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class ConfigMapCache<K, V> {
    private volatile ConcurrentHashMap<K, V> cachedConfig = null;

    public ConcurrentHashMap<K, V> get(Supplier<ConcurrentHashMap<K, V>> loader) {
        if (cachedConfig == null) {
            synchronized (this) {
                if (cachedConfig == null) {
                    cachedConfig = loader.get();
                }
            }
        }
        return cachedConfig;
    }
}
