package io.github.liana.config.core;

import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.exception.ConversionException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Defines the contract for resolving and converting configuration values from a data source.
 *
 * <p>This interface provides low-level operations used internally by higher-level
 * configuration abstractions such as {@link Configuration}. A {@code ValueResolver} focuses
 * exclusively on the mechanics of value lookup and type conversion, not on storage, caching, or
 * hierarchical behavior.
 *
 * <p>Implementations may retrieve configuration data from diverse sources, such as:
 * <ul>
 *   <li>Property files (e.g., {@code .properties}, {@code .yaml}, {@code .json})</li>
 *   <li>Environment variables or system properties</li>
 *   <li>Remote APIs or configuration services</li>
 *   <li>In-memory maps or composed resolvers</li>
 * </ul>
 *
 * <p>Resolution behavior:
 * <ul>
 *   <li>Values are accessed by their hierarchical key (e.g., {@code "server.port"})</li>
 *   <li>Conversion is type-safe, supporting primitives, generics, lists, maps, and POJOs</li>
 *   <li>Missing keys return empty results rather than {@code null}</li>
 *   <li>Implementations should avoid throwing exceptions for absent keys</li>
 * </ul>
 *
 * @see Configuration
 * @see AbstractConfiguration
 */
public interface ValueResolver {

  /**
   * Determines whether the specified key exists in the underlying configuration source.
   *
   * @param key the configuration key to check
   * @return {@code true} if the key is present and resolvable, {@code false} otherwise
   * @throws NullPointerException if {@code key} is {@code null}
   * @throws ConversionException  if the key cannot be resolved or conversion fails
   */
  boolean containsKey(String key);

  /**
   * Resolves a configuration value for the given key and converts it to the specified type.
   *
   * @param <T>  the expected result type
   * @param key  the configuration key
   * @param type the reflective type representing the target structure
   * @return an {@link Optional} containing the resolved and converted value, or empty if the key
   * does not exist
   * @throws NullPointerException if {@code key} or {@code type} is {@code null}
   * @throws ConversionException  if conversion to the specified type fails
   */
  <T> Optional<T> get(String key, Type type);

  /**
   * Resolves a configuration value as a list of elements of the specified type.
   *
   * <p>Each list element is individually converted to {@code clazz}. Implementations
   * may return an empty list if the key is missing or maps to no elements.
   *
   * @param <E>   the element type
   * @param key   the configuration key
   * @param clazz the element class
   * @return a list of converted elements, possibly empty but never {@code null}
   * @throws NullPointerException if {@code key} or {@code clazz} is {@code null}
   * @throws ConversionException  if any element cannot be converted to {@code clazz}
   */
  <E> List<E> getList(String key, Class<E> clazz);

  /**
   * Resolves a configuration value as a map with string keys and values of the specified type.
   *
   * <p>The returned map may represent a nested configuration object.
   * Implementations may return an empty map if the key is missing or empty.
   *
   * @param <V>   the value type
   * @param key   the configuration key
   * @param clazz the class representing the map value type
   * @return a map of key–value pairs, possibly empty but never {@code null}
   * @throws NullPointerException if {@code key} or {@code clazz} is {@code null}
   * @throws ConversionException  if conversion to {@code clazz} fails for any entry
   */
  <V> Map<String, V> getMap(String key, Class<V> clazz);

  /**
   * Returns the root configuration node as a raw, untyped map of keys and values.
   *
   * <p>This is typically used by higher-level abstractions to obtain the complete
   * configuration tree before mapping it to typed structures.
   *
   * @return a map representing the root configuration, possibly empty but never {@code null}
   * @throws ConversionException if the source cannot be read or parsed
   */
  Map<String, Object> getRootAsMap();

  /**
   * Converts the root configuration node to an object of the specified type.
   *
   * <p>This method allows for flexible deserialization of the entire configuration
   * into structured data models, such as POJOs, parameterized collections, or nested maps.
   *
   * @param <T>        the target type
   * @param targetType the reflective type representing the desired structure
   * @return an {@link Optional} containing the mapped root object, or empty if conversion fails or
   * the source is empty
   * @throws NullPointerException if {@code targetType} is {@code null}
   * @throws ConversionException  if conversion fails due to invalid or incompatible structure
   */
  <T> Optional<T> getRootAs(Type targetType);
}
