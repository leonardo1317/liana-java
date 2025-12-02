package io.github.liana.config.core;

/**
 * Functional interface for normalizing keys.
 *
 * <p>Implementations define a strategy to transform or standardize keys before they are used
 * in registries, maps, or other key-based data structures. Normalization can enforce
 * case-insensitivity, trimming, or other consistent formatting.
 *
 * <p>This interface is intended for internal use within registries and configuration components.
 * Implementations must be stateless and thread-safe if shared across multiple threads.
 *
 * @param <K> the type of key to normalize; must not be {@code null}
 */
@FunctionalInterface
public interface KeyNormalizer<K> {

  /**
   * Normalizes the given key according to the implementation's rules.
   *
   * @param key the key to normalize; must not be {@code null}
   * @return the normalized key; must not be {@code null}
   * @throws NullPointerException if {@code key} is {@code null}
   */
  K normalize(K key);
}
