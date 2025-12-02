package io.github.liana.config.core;

import static io.github.liana.config.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.type.TypeOf;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base implementation of the {@link Configuration} interface that delegates value resolution to a
 * {@link ValueResolver}.
 *
 * <p>This abstract class provides consistent behavior for configuration retrieval, including:
 * <ul>
 *   <li>Defensive copying of lists and maps to ensure immutability.</li>
 *   <li>Optional-based null handling for absent values.</li>
 *   <li>Validation of keys and type arguments.</li>
 * </ul>
 *
 * <p>Concrete subclasses are responsible for providing the underlying {@link ValueResolver},
 * which defines the actual mechanism to obtain and convert configuration values.
 *
 * <p><b>Thread Safety:</b> This class is thread-safe if the provided {@link ValueResolver} is
 * thread-safe.
 *
 * <p><b>Immutability:</b> Instances are effectively immutable; internal state cannot be modified
 * after construction.
 */
public abstract class AbstractConfiguration implements Configuration {

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
   *
   * <p>Validates that the key is non-null and non-blank, then delegates to {@link ValueResolver}.
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
   * <p>Delegates to {@link ValueResolver#get(String, Type)} to obtain and convert the value.
   *
   * @param <T>   the type of value
   * @param key   the key to look up
   * @param clazz the class of the value
   * @return an {@link Optional} containing the value, or empty if not found
   * @throws NullPointerException if {@code key} or {@code clazz} is null
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
   * <p>Delegates to {@link ValueResolver#get(String, Type)} for generic or parameterized types.
   *
   * @param <T>  the type of value
   * @param key  the key to look up
   * @param type the {@link TypeOf} representing the desired type
   * @return an {@link Optional} containing the value, or empty if not found
   * @throws NullPointerException if {@code key} or {@code type} is null
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
   * <p>Returns an immutable list. If the value is absent or empty, returns
   * {@link Collections#emptyList()}.
   *
   * @param <E>   the element type
   * @param key   the key to look up
   * @param clazz the class of list elements
   * @return an immutable list of values
   * @throws NullPointerException if {@code key} or {@code clazz} is null
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
   * <p>Returns an unmodifiable map. If the value is absent or empty, returns
   * {@link Collections#emptyMap()}.
   *
   * @param <V>   the value type
   * @param key   the key to look up
   * @param clazz the class of map values
   * @return an unmodifiable map of values
   * @throws NullPointerException if {@code key} or {@code clazz} is null
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
   * <p>Returns an unmodifiable view of the root configuration map. Safe for introspection or
   * diagnostics.
   *
   * @return the root configuration as an unmodifiable map
   */
  @Override
  public Map<String, Object> getRootAsMap() {
    Map<String, Object> result = resolver.getRootAsMap();
    return result.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(result);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Delegates to {@link ValueResolver#getRootAs(Type)} for conversion to a specific type.
   *
   * @param <T>  the target type
   * @param type the reflective type representing the desired structure
   * @return an {@link Optional} containing the converted root object, or empty if conversion fails
   * @throws NullPointerException if {@code type} is null
   */
  @Override
  public <T> Optional<T> getRootAs(Type type) {
    requireNonNull(type, TYPE_NULL_MSG);
    return resolver.getRootAs(type);
  }
}
