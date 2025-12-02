package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.ConfigurationManager;
import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Configuration;
import java.util.Map;

/**
 * Default implementation of {@link ConfigurationManager} that resolves configurations using a
 * {@link Pipeline} and caches the results.
 *
 * <p>This class is part of the internal resolution mechanism and is not intended to be subclassed.
 * It delegates all transformation logic to the configured pipeline and adds transparent caching for
 * performance.
 *
 * <h3>Thread-safety</h3>
 * <p>This implementation is thread-safe only if the provided {@link LoadingCache} and
 * {@link Pipeline} are themselves thread-safe. No additional synchronization is performed.
 *
 * <h3>Mutability</h3>
 * <p>The class is immutable after construction; the cache and pipeline references cannot change.
 */
public class DefaultConfigurationManager implements ConfigurationManager {

  private final LoadingCache<ResourceLocation, Map<String, Object>> cache;
  private final Pipeline pipeline;

  /**
   * Creates a new configuration manager with an empty default cache.
   *
   * @param pipeline the pipeline that performs loading, merging, and interpolation; must not be
   *                 null
   * @throws NullPointerException if {@code pipeline} is {@code null}
   */
  public DefaultConfigurationManager(Pipeline pipeline) {
    this(new LoadingCache<>(), pipeline);
  }

  /**
   * Creates a new configuration manager with an explicitly provided cache and pipeline.
   *
   * @param cache    the cache used to store previously resolved configurations; must not be null
   * @param pipeline the pipeline used to resolve configuration resources; must not be null
   * @throws NullPointerException if {@code cache} or {@code pipeline} is {@code null}
   */
  public DefaultConfigurationManager(
      LoadingCache<ResourceLocation, Map<String, Object>> cache, Pipeline pipeline
  ) {
    this.cache = requireNonNull(cache);
    this.pipeline = requireNonNull(pipeline);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation uses the provided {@link Pipeline} to resolve the configuration and
   * stores the result in the internal {@link LoadingCache}. Repeated calls with the same
   * {@code ResourceLocation} (as determined by its {@code equals} and {@code hashCode}) will return
   * the same cached result.
   *
   * <p>The cache key is the {@code ResourceLocation} instance itself, making the cache fully
   * type-safe and avoiding any string-based key construction.
   *
   * @return a {@link MapConfiguration} wrapping the resolved configuration
   * @throws NullPointerException if {@code location} is {@code null}
   */
  @Override
  public Configuration load(ResourceLocation location) {
    requireNonNull(location);

    Map<String, Object> result = cache.getOrCompute(location, () -> pipeline.execute(location));

    return new MapConfiguration(result);
  }
}
