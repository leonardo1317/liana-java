/**
 * Copyright 2025 Leonardo Favio Romero Silva
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.liana.config.api;

import io.github.liana.config.core.type.TypeOf;
import io.github.liana.config.core.ValueResolver;
import io.github.liana.config.core.exception.ConversionException;
import io.github.liana.config.core.exception.MissingConfigException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides a generic abstraction for accessing configuration data from various sources.
 *
 * <p>This interface supports reading configuration values by key and converting them into
 * specific types, including primitives, complex types, collections, and POJOs. Implementations may
 * load data from files, environment variables, remote sources, or composed configurations.
 *
 * <p>Values are resolved using a {@link ValueResolver}, which handles type conversion, defaults,
 * and nested structure access.
 */
public interface Configuration {

  /**
   * Determines whether the given key exists in the configuration source.
   *
   * @param key the configuration key to check
   * @return {@code true} if the configuration contains the key, {@code false} otherwise
   * @throws NullPointerException if {@code key} is {@code null}
   * @throws ConversionException  if the key cannot be resolved or the underlying data cannot be
   *                              parsed
   */
  boolean containsKey(String key);

  /**
   * Returns the configuration value associated with the given key, converted to the specified
   * type.
   *
   * @param key   the configuration key to look up (must not be {@code null})
   * @param clazz the expected class of the value (must not be {@code null})
   * @param <T>   the target class
   * @return an {@link Optional} containing the value if present and convertible, or an empty
   * optional otherwise
   * @throws NullPointerException if {@code key} or {@code clazz} is {@code null}
   * @throws ConversionException  if conversion fails or the key cannot be resolved
   */
  <T> Optional<T> get(String key, Class<T> clazz);

  /**
   * Returns the configuration value associated with the given key, converted to a generic type.
   *
   * @param key  the configuration key to look up (must not be {@code null})
   * @param type the expected generic type (must not be {@code null})
   * @param <T>  the target type
   * @return an {@link Optional} containing the value if present and convertible, or an empty
   * optional otherwise
   * @throws NullPointerException if {@code key} or {@code type} is {@code null}
   * @throws ConversionException  if conversion fails or the key cannot be resolved
   */
  <T> Optional<T> get(String key, TypeOf<T> type);

  /**
   * Retrieves the configuration value associated with the given key, or returns the specified
   * default value if the key is not present.
   *
   * <p>This method is useful for optional configuration values where a default should be applied
   * automatically.
   *
   * @param key          the configuration key to look up (must not be {@code null})
   * @param type         the expected generic type (must not be {@code null})
   * @param defaultValue the value to return if the key is absent
   * @param <T>          the target type
   * @return the configuration value associated with the key, or {@code defaultValue} if missing
   * @throws NullPointerException if {@code key} or {@code type} is {@code null}
   * @throws ConversionException  if conversion fails or the key cannot be resolved
   */
  default <T> T get(String key, TypeOf<T> type, T defaultValue) {
    return get(key, type).orElse(defaultValue);
  }

  /**
   * Retrieves the configuration value associated with the given key, or returns the specified
   * default value if the key is not present.
   *
   * <p>This method is useful for optional configuration values where a default should be applied
   * automatically.
   *
   * @param key          the configuration key to look up (must not be {@code null})
   * @param clazz        the expected class of the value (must not be {@code null})
   * @param defaultValue the value to return if the key is absent
   * @param <T>          the target type
   * @return the configuration value associated with the key, or {@code defaultValue} if missing
   * @throws NullPointerException if {@code key} or {@code clazz} is {@code null}
   * @throws ConversionException  if conversion fails or the key cannot be resolved
   */
  default <T> T get(String key, Class<T> clazz, T defaultValue) {
    return get(key, clazz).orElse(defaultValue);
  }

  /**
   * Convenience method for mandatory keys. Returns the value or throws
   * {@link MissingConfigException} if the key is missing.
   *
   * <p>This method simplifies access to required configuration values without using
   * {@link Optional}.
   *
   * @param key   the configuration key to look up (must not be {@code null})
   * @param clazz the expected class of the value (must not be {@code null})
   * @param <T>   the target type
   * @return the configuration value associated with the key
   * @throws NullPointerException   if {@code key} or {@code clazz} is {@code null}
   * @throws MissingConfigException if the key is not present in the configuration
   * @throws ConversionException    if conversion fails or the key cannot be resolved
   */
  default <T> T getOrThrow(String key, Class<T> clazz) {
    return get(key, clazz).orElseThrow(() -> missingConfigException(key));
  }

  /**
   * Convenience method for mandatory keys with generic types. Throws {@link MissingConfigException}
   * if the key is missing.
   *
   * <p>This method simplifies access to required configuration values of generic types without
   * using {@link Optional}.
   *
   * @param key  the configuration key to look up (must not be {@code null})
   * @param type the expected generic type (must not be {@code null})
   * @param <T>  the target type
   * @return the configuration value associated with the key
   * @throws NullPointerException   if {@code key} or {@code type} is {@code null}
   * @throws MissingConfigException if the key is not present in the configuration
   * @throws ConversionException    if conversion fails or the key cannot be resolved
   */
  default <T> T getOrThrow(String key, TypeOf<T> type) {
    return get(key, type).orElseThrow(() -> missingConfigException(key));
  }

  default String getString(String key) {
    return getOrThrow(key, String.class);
  }

  default int getInt(String key) {
    return getOrThrow(key, Integer.class);
  }

  default boolean getBoolean(String key) {
    return getOrThrow(key, Boolean.class);
  }

  default double getDouble(String key) {
    return getOrThrow(key, Double.class);
  }

  default Duration getDuration(String key) {
    return getOrThrow(key, Duration.class);
  }

  default String getString(String key, String defaultValue) {
    return get(key, String.class, defaultValue);
  }

  default int getInt(String key, int defaultValue) {
    return get(key, Integer.class, defaultValue);
  }

  default boolean getBoolean(String key, boolean defaultValue) {
    return get(key, Boolean.class, defaultValue);
  }

  default double getDouble(String key, double defaultValue) {
    return get(key, Double.class, defaultValue);
  }

  default Duration getDuration(String key, Duration defaultValue) {
    return get(key, Duration.class, defaultValue);
  }

  /**
   * Retrieves a configuration value as a list of elements of the specified type.
   *
   * <p>Each element in the list is converted to {@code clazz}. If the key does not exist or the
   * list is empty, an immutable empty list is returned.
   *
   * @param <E>   the element type
   * @param key   the configuration key
   * @param clazz the target element type
   * @return an immutable list of converted elements, or an empty list if no values are found
   * @throws NullPointerException if {@code key} or {@code clazz} is {@code null}
   * @throws ConversionException  if the target type is invalid or conversion fails
   */
  <E> List<E> getList(String key, Class<E> clazz);

  /**
   * Retrieves a configuration value as a map with string keys and values of the specified type.
   *
   * <p>The map may represent a nested configuration structure. The returned map is unmodifiable
   * and safe to share.
   *
   * @param <V>   the value type
   * @param key   the configuration key
   * @param clazz the target value type
   * @return an unmodifiable map of key–value pairs, or an empty map if the key does not exist
   * @throws NullPointerException if {@code key} or {@code clazz} is {@code null}
   * @throws ConversionException  if the target type is invalid or conversion fails
   */
  <V> Map<String, V> getMap(String key, Class<V> clazz);

  /**
   * Returns the root configuration node as a map of keys and values.
   *
   * <p>This method provides direct access to the entire configuration tree, typically used for
   * inspection or generic traversal purposes. The returned map is unmodifiable.
   *
   * @return an unmodifiable map representing the root configuration, or an empty map if none
   * @throws ConversionException if the underlying source cannot be read or parsed
   */
  Map<String, Object> getRootAsMap();

  /**
   * Converts the entire configuration root into the specified type.
   *
   * <p>This method supports complex or parameterized types, allowing conversion
   * into either a structured POJO or a generic container type (for example, {@code AppConfig},
   * {@code Map<String, Object>}, or {@code List<Service>}).
   *
   * @param <T>  the target type
   * @param type the reflective type representing the desired structure
   * @return an {@link Optional} containing the mapped object, or empty if the conversion fails
   * @throws NullPointerException if {@code type} is {@code null}
   * @throws ConversionException  if conversion fails or the structure cannot be mapped to the
   *                              target type
   */
  <T> Optional<T> getRootAs(Type type);

  private static MissingConfigException missingConfigException(String key) {
    return new MissingConfigException("Missing required config: " + key);
  }
}
