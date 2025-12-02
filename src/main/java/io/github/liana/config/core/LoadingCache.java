package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Thread-safe, in-memory cache that lazily loads values on demand.
 *
 * <p>This class maintains key-value pairs using a {@link ConcurrentHashMap}. If a value for a
 * given key is absent, it is computed using the provided {@link Supplier} and stored for future
 * retrieval.
 *
 * <p>This cache is immutable regarding its structure (keys and values cannot be externally
 * removed or modified except via {@link #getOrCompute}). It does not support expiration, eviction,
 * or refresh policies. Values may remain in memory for the lifetime of the application.
 *
 * <p>Thread Safety: Safe for concurrent access by multiple threads. Computation for the same key
 * is performed at most once.
 *
 * @param <K> the type of keys maintained by this cache; must not be {@code null}
 * @param <V> the type of mapped values; may be {@code null} if needed
 */
public final class LoadingCache<K, V> {

  private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

  /**
   * Returns the cached value associated with the given key, computing and storing it if absent.
   *
   * <p>If multiple threads attempt to compute the same key concurrently, only one computation
   * will be performed; all threads will receive the same cached result.
   *
   * @param key    the key whose associated value is to be returned or computed; must not be
   *               {@code null}
   * @param loader the function to compute a value if absent; must not be {@code null}
   * @return the existing or newly computed value associated with the key; may be {@code null}
   * @throws NullPointerException if {@code key} or {@code loader} is {@code null}
   */
  public V getOrCompute(K key, Supplier<V> loader) {
    requireNonNull(key, "key must not be null");
    requireNonNull(loader, "loader must not be null");
    return cache.computeIfAbsent(key, k -> loader.get());
  }
}
