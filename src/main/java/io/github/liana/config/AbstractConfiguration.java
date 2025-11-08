package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base implementation of the {@link Configuration} interface that delegates value resolution to a
 * {@link ValueResolver}.
 *
 * <p>This class provides consistent behavior for all configuration retrieval methods,
 * including defensive copying, immutability guarantees, and optional-based null handling.
 *
 * <p>Concrete implementations only need to provide the underlying {@link ValueResolver},
 * which defines how values are obtained and converted.
 */
abstract class AbstractConfiguration implements Configuration {

  private static final String KEY_NULL_MSG = "key must not be null";
  private static final String KEY_BLANK_MSG = "key must not be blank";
  private static final String CLAZZ_NULL_MSG = "clazz must not be null";
  private static final String TYPE_NULL_MSG = "type must not be null";

  private final ValueResolver resolver;

  /**
   * Creates a new {@code AbstractConfiguration} with the given {@link ValueResolver}.
   *
   * @param resolver the resolver responsible for resolving and converting configuration values
   * @throws NullPointerException if {@code resolver} is {@code null}
   */
  protected AbstractConfiguration(ValueResolver resolver) {
    this.resolver = requireNonNull(resolver, "resolver must not be null");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(String key) {
    requireNonNull(key, KEY_NULL_MSG);
    requireNonBlank(key, KEY_BLANK_MSG);
    return resolver.containsKey(key);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Delegates directly to the underlying {@link ValueResolver}, supporting complex and generic
   * class resolution such as lists, maps, and POJOs.
   */
  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    requireNonNull(key, KEY_NULL_MSG);
    requireNonBlank(key, KEY_BLANK_MSG);
    requireNonNull(clazz, CLAZZ_NULL_MSG);
    return resolver.get(key, clazz);
  }

  /**
   * {@inheritDoc}
   *
   */
  @Override
  public <T> Optional<T> get(String key, TypeOf<T> type) {
    requireNonNull(key, KEY_NULL_MSG);
    requireNonBlank(key, KEY_BLANK_MSG);
    requireNonNull(type, TYPE_NULL_MSG);
    return resolver.get(key, type.getType());
  }

  /**
   * {@inheritDoc}
   *
   * <p>The returned list is immutable and safe to share. If no values are found, returns
   * {@link Collections#emptyList()}.
   */
  @Override
  public <E> List<E> getList(String key, Class<E> clazz) {
    requireNonNull(key, KEY_NULL_MSG);
    requireNonBlank(key, KEY_BLANK_MSG);
    requireNonNull(clazz, CLAZZ_NULL_MSG);
    List<E> result = resolver.getList(key, clazz);
    return result.isEmpty() ? Collections.emptyList() : List.copyOf(result);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The returned map is unmodifiable and safe to share. If no entries are found, returns
   * {@link Collections#emptyMap()}.
   */
  @Override
  public <V> Map<String, V> getMap(String key, Class<V> clazz) {
    requireNonNull(key, KEY_NULL_MSG);
    requireNonBlank(key, KEY_BLANK_MSG);
    requireNonNull(clazz, CLAZZ_NULL_MSG);
    Map<String, V> result = resolver.getMap(key, clazz);
    return result.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(result);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Provides direct access to the root configuration as an unmodifiable map. This is often used
   * for diagnostic or introspection purposes.
   */
  @Override
  public Map<String, Object> getRootAsMap() {
    Map<String, Object> result = resolver.getRootAsMap();
    return result.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(result);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation delegates to {@link ValueResolver#getRootAs(Type)}, performing
   * the conversion of the entire configuration tree into an instance of the specified type.
   *
   * <p>The resulting object can represent a structured POJO, a generic collection,
   * or any complex type supported by the underlying resolver.
   *
   * @param <T>  the target type
   * @param type the reflective type representing the desired structure
   * @return an {@link Optional} containing the converted root object, or empty if conversion fails
   */
  @Override
  public <T> Optional<T> getRootAs(Type type) {
    requireNonNull(type, TYPE_NULL_MSG);
    return resolver.getRootAs(type);
  }
}
