package io.github.liana.config;

import io.github.liana.config.exception.ConversionException;
import java.lang.reflect.Type;
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
   * @throws ConversionException  if the key cannot be resolved or the underlying data cannot be parsed
   *
   */
  boolean containsKey(String key);

  /**
   * Retrieves a configuration value for the given key and converts it to the specified type.
   *
   * <p>This is the most flexible retrieval method. It supports:
   * <ul>
   *   <li>Simple types (e.g. {@code String.class}, {@code Integer.class}).</li>
   *   <li>Parameterized types (e.g. {@code List<String>}, {@code Map<String, Integer>}).</li>
   *   <li>Nesting combinations (e.g. {@code Map<String, List<MyPojo>>},
   *       {@code List<Map<String, MyPojo>>}, {@code Map<String, Map<String, MyPojo>>}).</li>
   *   <li>Mapping to POJOs or records representing structured configuration.</li>
   * </ul>
   *
   * <p>Example:
   * <pre>{@code
   * Type t = new TypeReference<Map<String, List<AppConfig>>>() {}.getType();
   * Optional<Map<String, List<AppConfig>>> cfg = configuration.get("clients", t);
   * }</pre>
   *
   * @param <T> the expected result type
   * @param key the configuration key
   * @param type the target type to convert the value into (maybe a {@link Class} or any reflective {@link Type})
   * @return an {@link Optional} containing the resolved value, or empty if the key does not exist
   * @throws NullPointerException if {@code key} or {@code type} is {@code null}
   * @throws ConversionException  if conversion fails or the key cannot be resolved
   */
  <T> Optional<T> get(String key, Type type);

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
   * into either a structured POJO or a generic container type (for example,
   * {@code AppConfig}, {@code Map<String, Object>}, or {@code List<Service>}).
   *
   * @param <T>  the target type
   * @param type the reflective type representing the desired structure
   * @return an {@link Optional} containing the mapped object, or empty if the conversion fails
   * @throws NullPointerException if {@code type} is {@code null}
   * @throws ConversionException  if conversion fails or the structure cannot be mapped to the target type
   */
  <T> Optional<T> getRootAs(Type type);
}
