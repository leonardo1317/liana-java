package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A registry for {@link Strategy} implementations, keyed by values of type {@code K}.
 *
 * <p>This class provides a way to register and look up strategies by one or more keys.
 * Keys are normalized using a {@link KeyNormalizer} before being stored in the registry. The
 * registry is immutable once created.
 *
 * <p>All constructors enforce non-null arguments and copy their inputs defensively
 * to guarantee immutability and null-safety. Duplicate keys will cause the last registered strategy
 * for that key to override the previous one.
 *
 * <p>Instances are thread-safe due to immutability.
 *
 * @param <K> the type of keys used to look up strategies
 * @param <V> the type of strategy stored, must implement {@link Strategy}
 */
public class StrategyRegistry<K, V extends Strategy<K>> {

  private final Map<K, V> strategies;
  private final KeyNormalizer<K> keyNormalizer;
  private static final String KEY_MUST_NOT_BE_NULL = "key must not be null";
  private static final String KEY_NORMALIZER_MUST_NOT_BE_NULL = "keyNormalizer must not be null";
  private static final String STRATEGIES_VARARGS_ARRAY_MUST_NOT_BE_NULL = "strategies varargs array must not be null";
  private static final String STRATEGIES_COLLECTIONS_MUST_NOT_BE_NULL = "strategies collections must not be null";

  /**
   * Creates a registry with the given strategies using the default key normalizer.
   *
   * @param strategies the strategies to register
   * @throws NullPointerException if the array or any contained strategy is null
   */
  @SafeVarargs
  public StrategyRegistry(V... strategies) {
    this(defaultNormalizer(), strategies);
  }

  /**
   * Creates a registry with the given strategies and a custom key normalizer.
   *
   * @param keyNormalizer the normalizer to apply to keys
   * @param strategies    the strategies to register
   * @throws NullPointerException if the normalizer, array, or any contained strategy is null
   */
  @SafeVarargs
  public StrategyRegistry(KeyNormalizer<K> keyNormalizer, V... strategies) {
    this(keyNormalizer,
        List.of(requireNonNull(strategies, STRATEGIES_VARARGS_ARRAY_MUST_NOT_BE_NULL)));
  }

  /**
   * Creates a registry with the given strategies and a custom key normalizer.
   *
   * @param keyNormalizer the normalizer to apply to keys
   * @param strategies    the strategies to register
   * @throws NullPointerException if the normalizer, collection, or any contained strategy is null
   */
  public StrategyRegistry(KeyNormalizer<K> keyNormalizer, Collection<V> strategies) {
    var safeStrategies = List.copyOf(
        requireNonNull(strategies, STRATEGIES_COLLECTIONS_MUST_NOT_BE_NULL));
    this.keyNormalizer = requireNonNull(keyNormalizer, KEY_NORMALIZER_MUST_NOT_BE_NULL);
    var strategiesMap = new LinkedHashMap<K, V>();
    safeStrategies.forEach(strategy -> put(strategiesMap, strategy));
    this.strategies = Collections.unmodifiableMap((strategiesMap));
  }

  /**
   * Registers the given strategy into the provided map by associating each of its keys (after
   * normalization) with the strategy instance.
   *
   * @param strategiesMap the mutable map where strategies are registered
   * @param strategy      the strategy to register, must not be {@code null}
   * @throws NullPointerException if {@code strategy} or any of its keys is {@code null}
   */
  private void put(Map<K, V> strategiesMap, V strategy) {
    Collection<K> keys = List.copyOf(strategy.getKeys());
    for (K key : keys) {
      K normalizedKey = keyNormalizer.normalize(key);
      strategiesMap.put(normalizedKey, strategy);
    }
  }

  /**
   * Returns the strategy associated with the given key, if present.
   *
   * @param key the key to look up
   * @return an {@link Optional} containing the strategy, or empty if none exists
   * @throws NullPointerException if the key is null
   */
  public Optional<V> get(K key) {
    requireNonNull(key, KEY_MUST_NOT_BE_NULL);
    return Optional.ofNullable(strategies.get(keyNormalizer.normalize(key)));
  }

  /**
   * Returns all registered keys in the order they were inserted.
   *
   * @return an unmodifiable set of all keys
   */
  public Set<K> getAllKeys() {
    return Collections.unmodifiableSet(strategies.keySet());
  }

  /**
   * Returns the default key normalizer, which simply enforces non-null keys.
   *
   * @param <K> the key type
   * @return a default key normalizer
   */
  private static <K> KeyNormalizer<K> defaultNormalizer() {
    return key -> requireNonNull(key, KEY_MUST_NOT_BE_NULL);
  }
}
