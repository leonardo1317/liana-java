package io.github.liana.config;

import io.github.liana.config.exception.MissingConfigException;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfigReader {

    default int getInt(String key) {
        return getOrThrow(key, Integer.class);
    }

    default int getInt(String key, int defaultValue) {
        return get(key, Integer.class).orElse(defaultValue);
    }

    default long getLong(String key) {
        return getOrThrow(key, Long.class);
    }

    default long getLong(String key, long defaultValue) {
        return get(key, Long.class).orElse(defaultValue);
    }

    default boolean getBoolean(String key) {
        return getOrThrow(key, Boolean.class);
    }

    default boolean getBoolean(String key, boolean defaultValue) {
        return get(key, Boolean.class).orElse(defaultValue);
    }

    default float getFloat(String key) {
        return getOrThrow(key, Float.class);
    }

    default float getFloat(String key, float defaultValue) {
        return get(key, Float.class).orElse(defaultValue);
    }

    default double getDouble(String key) {
        return getOrThrow(key, Double.class);
    }

    default double getDouble(String key, double defaultValue) {
        return get(key, Double.class).orElse(defaultValue);
    }

    default String getString(String key) {
        return getOrThrow(key, String.class);
    }

    default String getString(String key, String defaultValue) {
        return get(key, String.class).orElse(defaultValue);
    }

    default Duration getDuration(String key) {
        return getOrThrow(key, Duration.class);
    }

    default Duration getDuration(String key, Duration defaultValue) {
        return get(key, Duration.class).orElse(defaultValue);
    }

    boolean hasKey(String key);

    <T> Optional<T> get(String key, Class<T> clazz);

    <T> Optional<T> get(String key, TypeOf<T> typeRef);

    default <T> T getOrThrow(String key, Class<T> clazz) {
        return get(key, clazz)
                .orElseThrow(() -> new MissingConfigException("Missing required config: " + key));
    }

    default <T> T getOrThrow(String key, TypeOf<T> typeRef) {
        return get(key, typeRef)
                .orElseThrow(() -> new MissingConfigException("Missing required config: " + key));
    }

    default <E> List<E> getList(String key, Class<E> clazz) {
        return getList(key, clazz, Collections.emptyList());
    }

    <E> List<E> getList(String key, Class<E> clazz, List<E> defaultValue);

    default <V> Map<String, V> getMap(String key, Class<V> clazz) {
        return getMap(key, clazz, Collections.emptyMap());
    }

    <V> Map<String, V> getMap(String key, Class<V> clazz, Map<String, V> defaultValue);

    default List<String> getStringList(String key) {
        return getList(key, String.class);
    }

    default List<String> getStringList(String key, List<String> defaultValue) {
        return getList(key, String.class, defaultValue);
    }

    default String[] getStringArray(String key) {
        return getOrThrow(key, new TypeOf<>() {});
    }

    default String[] getStringArray(String key, String[] defaultValue) {
        return get(key, new TypeOf<String[]>() {}).orElse(defaultValue);
    }

    default Map<String, String> getStringMap(String key) {
        return getMap(key, String.class);
    }

    default Map<String, String> getStringMap(String key, Map<String, String> defaultValue) {
        return getMap(key, String.class, defaultValue);
    }

    Map<String, Object> getAllConfig();

    <T> Optional<T> getAllConfigAs(Class<T> clazz);
}
