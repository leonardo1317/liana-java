package io.github.liana.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

final class ConfigMapCache<K, V> {
    private volatile Map<K, V> cachedConfig = null;

    public Map<K, V> get(Supplier<Map<K, V>> loader) {
        if (cachedConfig == null) {
            synchronized (this) {
                if (cachedConfig == null) {
                    cachedConfig = Collections.unmodifiableMap(new LinkedHashMap<>(loader.get()));
                }
            }
        }
        return cachedConfig;
    }
}
