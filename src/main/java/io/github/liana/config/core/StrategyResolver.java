package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Generic resolver for strategies keyed by a lookup value.
 *
 * <p>This class delegates strategy selection to a {@link StrategyRegistry}.
 * If no strategy matches the provided key, it throws a type-specific exception supplied by the
 * configured exception factory.</p>
 *
 * <p>Instances are immutable and thread-safe.</p>
 *
 * @param <K> the type of lookup key
 * @param <V> the type of resolved strategy
 */
public class StrategyResolver<K, V extends Strategy<K>> {

  private final StrategyRegistry<K, V> registry;
  private final Function<K, RuntimeException> exceptionFactory;

  /**
   * Creates a new {@code StrategyResolver}.
   *
   * @param registry         the registry containing the strategies; must not be {@code null}
   * @param exceptionFactory factory to create the exception when a strategy is missing; must not be
   *                         {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public StrategyResolver(
      StrategyRegistry<K, V> registry,
      Function<K, RuntimeException> exceptionFactory
  ) {
    this.registry = requireNonNull(registry, "registry must not be null");
    this.exceptionFactory =
        requireNonNull(exceptionFactory, "exceptionFactory must not be null");
  }

  /**
   * Resolves a strategy for the given key.
   *
   * @param key the lookup key; must not be {@code null}
   * @return the resolved strategy
   * @throws RuntimeException if the key does not match any registered strategy
   */
  public V resolve(K key) {
    requireNonNull(key, "key must not be null");
    return registry.get(key)
        .orElseThrow(() -> exceptionFactory.apply(key));
  }
}
