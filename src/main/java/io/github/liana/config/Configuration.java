package io.github.liana.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Configuration {
    boolean hasKey(String key);

    <T> Optional<T> get(String key, Type type);

    <E> List<E> getList(String key, Class<E> clazz);

    <V> Map<String, V> getMap(String key, Class<V> clazz);

    Map<String, Object> getAllConfig();

    <T> Optional<T> getAllConfigAs(Class<T> clazz);
}
