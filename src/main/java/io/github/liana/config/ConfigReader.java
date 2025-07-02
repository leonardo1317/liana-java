/**
 * Copyright 2025 Leonardo Favio Romero Silva
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p><a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */

package io.github.liana.config;

import io.github.liana.config.exception.MissingConfigException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides methods for accessing configuration values from various sources in a type-safe manner.
 * This interface allows retrieval of configuration values by key and supports default values, type
 * conversion, and optional values.
 *
 * <p>Implementations must ensure null-safety and appropriate type handling.</p>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *   <li>{@link NullPointerException} if the provided {@code key}, {@code Class} type,
 *   or {@code TypeOf} reference is {@code null}.</li>
 *   <li>{@link IllegalArgumentException} if the provided {@code key} is blank
 *   (empty or contains only whitespace).</li>
 *   <li>{@link MissingConfigException} if a required configuration key is missing and no default
 *   value is provided (in methods like {@code getOrThrow}).</li>
 * </ul>
 */
public interface ConfigReader {

  /**
   * Retrieves the int value associated with the given key.
   *
   * @param key the configuration key (must not be blank)
   * @return the int value
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key does not exist or the value cannot be converted
   */
  default int getInt(String key) {
    return getOrThrow(key, Integer.class);
  }

  /**
   * Retrieves the int value associated with the given key, or returns a default value if absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default value to return if the key is not present
   * @return the int value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default int getInt(String key, int defaultValue) {
    return get(key, Integer.class).orElse(defaultValue);
  }

  /**
   * Retrieves the long value associated with the given key.
   *
   * @param key the configuration key (must not be blank)
   * @return the long value
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key does not exist or the value cannot be converted
   */
  default long getLong(String key) {
    return getOrThrow(key, Long.class);
  }

  /**
   * Retrieves the long value associated with the given key, or returns a default value if absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default value to return if the key is not present
   * @return the long value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default long getLong(String key, long defaultValue) {
    return get(key, Long.class).orElse(defaultValue);
  }

  /**
   * Retrieves the boolean value associated with the given key.
   *
   * @param key the configuration key (must not be blank)
   * @return the boolean value
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key does not exist or the value cannot be converted
   */
  default boolean getBoolean(String key) {
    return getOrThrow(key, Boolean.class);
  }

  /**
   * Retrieves the boolean value associated with the given key, or returns a default value if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default value to return if the key is not present
   * @return the boolean value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default boolean getBoolean(String key, boolean defaultValue) {
    return get(key, Boolean.class).orElse(defaultValue);
  }

  /**
   * Retrieves the float value associated with the given key.
   *
   * @param key the configuration key (must not be blank)
   * @return the float value
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key does not exist or the value cannot be converted
   */
  default float getFloat(String key) {
    return getOrThrow(key, Float.class);
  }

  /**
   * Retrieves the float value associated with the given key, or returns a default value if absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default value to return if the key is not present
   * @return the float value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default float getFloat(String key, float defaultValue) {
    return get(key, Float.class).orElse(defaultValue);
  }

  /**
   * Retrieves the double value associated with the given key.
   *
   * @param key the configuration key (must not be blank)
   * @return the double value
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key does not exist or the value cannot be converted
   */
  default double getDouble(String key) {
    return getOrThrow(key, Double.class);
  }

  /**
   * Retrieves the double value associated with the given key, or returns a default value if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default value to return if the key is not present
   * @return the double value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default double getDouble(String key, double defaultValue) {
    return get(key, Double.class).orElse(defaultValue);
  }

  /**
   * Retrieves the String value associated with the given key.
   *
   * @param key the configuration key (must not be blank)
   * @return the String value
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key does not exist or the value cannot be converted
   */
  default String getString(String key) {
    return getOrThrow(key, String.class);
  }

  /**
   * Retrieves the String value associated with the given key, or returns a default value if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default value to return if the key is not present
   * @return the String value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default String getString(String key, String defaultValue) {
    return get(key, String.class).orElse(defaultValue);
  }

  /**
   * Checks whether the configuration contains the specified key.
   *
   * @param key the configuration key (must not be blank)
   * @return {@code true} if the key exists; {@code false} otherwise
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  boolean hasKey(String key);

  /**
   * Retrieves the string array associated with the specified key.
   *
   * @param key the configuration key (must not be blank)
   * @return the string array, or an empty array if not present
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default String[] getStringArray(String key) {
    return getStringArray(key, new String[0]);
  }

  /**
   * Retrieves the string array associated with the specified key, or returns a default array if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default array to return if the key is not present
   * @return the string array or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default String[] getStringArray(String key, String[] defaultValue) {
    return get(key, new TypeOf<String[]>() {
    }).orElse(defaultValue);
  }

  /**
   * Retrieves the value associated with the specified key and type, or throws an exception if
   * missing.
   *
   * @param key   the configuration key (must not be blank)
   * @param clazz the expected value type (must not be {@code null})
   * @param <T>   the value type
   * @return the value
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key is missing or cannot be converted
   */
  default <T> T getOrThrow(String key, Class<T> clazz) {
    return get(key, clazz)
        .orElseThrow(() -> missingConfigException(key));
  }

  /**
   * Retrieves the value of a complex or generic type, or throws an exception if missing.
   *
   * @param key  the configuration key (must not be blank)
   * @param type the type reference (must not be {@code null})
   * @param <T>  the value type
   * @return the value
   * @throws NullPointerException     if {@code key} or {@code type} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   * @throws MissingConfigException   if the key is missing or cannot be converted
   */
  default <T> T getOrThrow(String key, TypeOf<T> type) {
    return get(key, type)
        .orElseThrow(() -> missingConfigException(key));
  }

  /**
   * Retrieves the value of a complex or generic type, or returns a default value if absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param type         the type reference (must not be {@code null})
   * @param defaultValue the default value to return if the key is not present
   * @param <T>          the value type
   * @return the value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} or {@code type} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default <T> T get(String key, TypeOf<T> type, T defaultValue) {
    return get(key, type).orElse(defaultValue);
  }

  /**
   * Retrieves an optional value of a complex or generic type.
   *
   * @param key  the configuration key (must not be blank)
   * @param type the type reference (must not be {@code null})
   * @param <T>  the value type
   * @return an {@code Optional} containing the value if present, or {@code Optional.empty()}
   * @throws NullPointerException     if {@code key} or {@code type} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  <T> Optional<T> get(String key, TypeOf<T> type);

  /**
   * Retrieves the value associated with the specified key and type, or returns a default value if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param clazz        the expected value type (must not be {@code null})
   * @param defaultValue the default value to return if the key is not present
   * @param <T>          the value type
   * @return the value or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default <T> T get(String key, Class<T> clazz, T defaultValue) {
    return get(key, clazz).orElse(defaultValue);
  }

  /**
   * Retrieves an optional value associated with the specified key and type.
   *
   * @param key   the configuration key (must not be blank)
   * @param clazz the expected value type (must not be {@code null})
   * @param <T>   the value type
   * @return an {@code Optional} with the value if present, or {@code Optional.empty()}
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  <T> Optional<T> get(String key, Class<T> clazz);

  /**
   * Retrieves the string list associated with the specified key.
   *
   * @param key the configuration key (must not be blank)
   * @return the string list, or an empty list if not present
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default List<String> getStringList(String key) {
    return getStringList(key, Collections.emptyList());
  }

  /**
   * Retrieves the string list associated with the specified key, or returns a default list if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default list to return if the key is not present
   * @return the string list or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default List<String> getStringList(String key, List<String> defaultValue) {
    return getList(key, String.class, defaultValue);
  }

  /**
   * Retrieves the list of values associated with the specified key and type.
   *
   * @param key   the configuration key (must not be blank)
   * @param clazz the expected element type (must not be {@code null})
   * @param <E>   the element type
   * @return the list of values, or an empty list if not present
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default <E> List<E> getList(String key, Class<E> clazz) {
    return getList(key, clazz, Collections.emptyList());
  }

  /**
   * Retrieves the list of values associated with the specified key and type, or returns a default
   * list if absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param clazz        the expected element type (must not be {@code null})
   * @param defaultValue the default list to return if the key is not present
   * @param <E>          the element type
   * @return the list of values or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  <E> List<E> getList(String key, Class<E> clazz, List<E> defaultValue);

  /**
   * Retrieves the string map associated with the specified key.
   *
   * @param key the configuration key (must not be blank)
   * @return the string map, or an empty map if not present
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default Map<String, String> getStringMap(String key) {
    return getStringMap(key, Collections.emptyMap());
  }

  /**
   * Retrieves the string map associated with the specified key, or returns a default map if
   * absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param defaultValue the default map to return if the key is not present
   * @return the string map or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default Map<String, String> getStringMap(String key, Map<String, String> defaultValue) {
    return getMap(key, String.class, defaultValue);
  }

  /**
   * Retrieves the map of values associated with the specified key and value type.
   *
   * @param key   the configuration key (must not be blank)
   * @param clazz the expected value type (must not be {@code null})
   * @param <V>   the value type
   * @return the map of values, or an empty map if not present
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  default <V> Map<String, V> getMap(String key, Class<V> clazz) {
    return getMap(key, clazz, Collections.emptyMap());
  }

  /**
   * Retrieves the map of values associated with the specified key and value type, or returns a
   * default map if absent.
   *
   * @param key          the configuration key (must not be blank)
   * @param clazz        the expected value type (must not be {@code null})
   * @param defaultValue the default map to return if the key is not present
   * @param <V>          the value type
   * @return the map of values or {@code defaultValue} if not found
   * @throws NullPointerException     if {@code key} or {@code clazz} is {@code null}
   * @throws IllegalArgumentException if {@code key} is blank
   */
  <V> Map<String, V> getMap(String key, Class<V> clazz, Map<String, V> defaultValue);

  /**
   * Retrieves all configuration entries as an unmodifiable map.
   *
   * @return the entire configuration as a {@code Map<String, Object>}
   */
  Map<String, Object> getAllConfig();

  /**
   * Retrieves the entire configuration deserialized as an object of the specified type.
   *
   * @param clazz the expected type (must not be {@code null})
   * @param <T>   the target type
   * @return an {@code Optional} containing the deserialized object if present
   * @throws NullPointerException if {@code clazz} is {@code null}
   */
  <T> Optional<T> getAllConfigAs(Class<T> clazz);

  private static MissingConfigException missingConfigException(String key) {
    return new MissingConfigException("Missing required config: " + key);
  }
}
