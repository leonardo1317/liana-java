package io.github.liana.config.core;

/**
 * Functional interface representing a source of property values.
 *
 * <p>This interface provides a single method to retrieve a value for a given key.
 * Implementations can be backed by environment variables, maps, configuration files, or any other
 * key-value store.
 *
 * <p>This interface is intended for internal and public API usage where flexible
 * property resolution is required. Implementations must handle `null` keys gracefully if
 * applicable.
 *
 * <p>Instances are typically stateless and thread-safe, but thread-safety depends
 * on the backing implementation.
 */
@FunctionalInterface
public interface PropertySource {

  /**
   * Retrieves the property value associated with the given key.
   *
   * @param key the property key to look up; must not be {@code null}
   * @return the property value as a string, or {@code null} if no value is present for the key
   * @throws NullPointerException if {@code key} is {@code null} and the implementation does not
   *                              allow it
   */
  String get(String key);
}
