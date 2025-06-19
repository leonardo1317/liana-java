package io.github.liana.config;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.liana.internal.JsonPathAccessor;

import static java.util.Objects.requireNonNull;

abstract class AbstractConfiguration implements Configuration {
    private final Map<String, Object> nestedMap;

    protected AbstractConfiguration(Map<String, Object> nestedMap) {
        requireNonNull(nestedMap, "store type must not be null");
        this.nestedMap = Collections.unmodifiableMap(new LinkedHashMap<>(nestedMap));
    }

    @Override
    public boolean hasKey(String key) {
        return JsonPathAccessor.hasPath(nestedMap, key);
    }

    @Override
    public <T> Optional<T> get(String key, Type type) {
        return JsonPathAccessor.get(nestedMap, key, type);
    }

    @Override
    public <E> List<E> getList(String key, Class<E> clazz) {
        List<E> result = JsonPathAccessor.getList(nestedMap, key, clazz);
        return result.isEmpty() ? Collections.emptyList() : List.copyOf(result);
    }

    @Override
    public <V> Map<String, V> getMap(String key, Class<V> clazz) {
        Map<String, V> result = JsonPathAccessor.getMap(nestedMap, key, clazz);
        return result.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(result);
    }

    @Override
    public Map<String, Object> getAllConfig() {
        return nestedMap;
    }

    @Override
    public <T> Optional<T> getAllConfigAs(Class<T> clazz) {
        return JsonPathAccessor.get(nestedMap, clazz);
    }
}
