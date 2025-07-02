package io.github.liana.internal;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable key-value configuration map that enforces read-only access.
 *
 * <p>This class wraps a {@link Map} and ensures that its content cannot be modified after
 * creation.
 * Keys and values are strings, and access is restricted through a controlled API that validates
 * non-null keys.
 *
 * <p>It is commonly used to expose configuration data safely.
 *
 * <p>Instances can be created via static factory methods:
 * <ul>
 *     <li>{@link #empty()} to get an empty instance</li>
 *     <li>{@link #of(Map)} to wrap an existing map</li>
 * </ul>
 */
public class ImmutableConfigMap {

  private final Map<String, String> map;

  /**
   * Creates a new immutable configuration map from the given map. The internal representation is
   * unmodifiable.
   *
   * @param map the source map to wrap
   */
  private ImmutableConfigMap(Map<String, String> map) {
    this.map = Collections.unmodifiableMap(map);
  }

  /**
   * Returns an empty immutable configuration map.
   *
   * @return an empty {@code ImmutableConfigMap} instance
   */
  public static ImmutableConfigMap empty() {
    return new ImmutableConfigMap(Collections.emptyMap());
  }

  /**
   * Creates an immutable configuration map using the provided key-value pairs. Keys are inserted in
   * the order defined by {@link LinkedConfigMap}.
   *
   * @param map the map to wrap
   * @return an immutable configuration map
   * @throws NullPointerException if {@code map} is {@code null}
   */
  public static ImmutableConfigMap of(Map<String, String> map) {
    return new ImmutableConfigMap(new LinkedConfigMap(map));
  }

  /**
   * Retrieves the value associated with the given key.
   *
   * @param key a non-null key
   * @return the associated value, or {@code null} if the key is not present
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public String get(String key) {
    requireNonNull(key);
    return map.get(key);
  }

  /**
   * Retrieves the value associated with the given key, or returns the default value if the key is
   * not present.
   *
   * @param key          the key whose associated value is to be returned
   * @param defaultValue the default value to return if the key is not found
   * @return the associated value, or {@code defaultValue} if the key is not present
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public String getOrDefault(String key, String defaultValue) {
    requireNonNull(key);
    return map.getOrDefault(key, defaultValue);
  }

  /**
   * Checks whether the given key is present in the map.
   *
   * @param key a non-null key
   * @return {@code true} if the key exists, {@code false} otherwise
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public boolean containsKey(String key) {
    requireNonNull(key);
    return map.containsKey(key);
  }

  /**
   * Returns {@code true} if the map contains no key-value mappings.
   *
   * @return {@code true} if empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /**
   * Returns the number of key-value mappings in this map.
   *
   * @return the size of the map
   */
  public int size() {
    return map.size();
  }

  /**
   * Returns an unmodifiable view of the underlying map.
   *
   * @return an unmodifiable map of keys and values
   */
  public Map<String, String> toMap() {
    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImmutableConfigMap)) {
      return false;
    }
    ImmutableConfigMap that = (ImmutableConfigMap) o;
    return Objects.equals(map, that.map);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(map);
  }

  @Override
  public String toString() {
    return "ImmutableConfigMap" + map.toString();
  }
}
