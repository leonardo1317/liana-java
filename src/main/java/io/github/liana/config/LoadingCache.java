package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A simple thread-safe cache that lazily loads values on demand.
 *
 * <p>This cache stores key-value pairs in memory using a {@link ConcurrentHashMap}.
 * If a value for a given key is not present, it will be computed using the provided
 * {@link Supplier} and stored in the cache for future lookups.
 *
 * <p>This implementation does not support expiration, size limits, or refresh policies.
 * It is intended for lightweight, concurrent use cases where cached data can safely remain valid
 * for the application's lifetime.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * LoadingCache<String, JsonNode> cache = new LoadingCache<>();
 * JsonNode node = cache.getOrCompute("path", () -> computeJsonNode());
 * }</pre>
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
final class LoadingCache<K, V> {

  private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

  /**
   * Returns the cached value associated with the given key, or computes and stores it if not
   * already present.
   *
   * <p>If multiple threads attempt to compute the same key concurrently, the computation
   * will be performed only once. Subsequent calls will return the same cached value.
   *
   * @param key    the key whose associated value is to be returned or computed
   * @param loader a {@link Supplier} that provides the value if it is not already cached
   * @return the existing or newly computed value associated with the key
   * @throws NullPointerException if {@code key} or {@code loader} is {@code null}
   */
  public V getOrCompute(K key, Supplier<V> loader) {
    requireNonNull(key, "key must not be null");
    requireNonNull(loader, "loader must not be null");
    return cache.computeIfAbsent(key, k -> loader.get());
  }
}
