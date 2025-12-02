package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.providers.ClasspathProvider;
import io.github.liana.config.spi.ResourceProvider;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Registry and factory for {@link ResourceProvider} instances.
 *
 * <p>This class maintains a set of custom configuration providers and can produce a
 * {@link StrategyRegistry} combining custom providers with default providers (e.g., classpath
 * provider). All keys in the registry are normalized to lowercase.
 *
 * <p>Instances are immutable after construction. This is an internal class and not part of the
 * public API.
 *
 * <p><strong>Responsibilities:</strong>
 * <ul>
 *   <li>Store custom configuration providers.</li>
 *   <li>Create a {@link StrategyRegistry} containing default and custom providers.</li>
 * </ul>
 *
 * <p>Limitations: The key normalizer is fixed to lowercase. Thread-safety is guaranteed only for
 * immutable use (no mutation after construction).
 */
public final class ProvidersRegistry {

  private final List<ResourceProvider> customProviders;
  private final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);

  /**
   * Constructs a new registry with the given custom providers.
   *
   * @param customProviders the custom {@link ResourceProvider} instances; must not be {@code null}
   * @throws NullPointerException if {@code customProviders} is {@code null}
   */
  public ProvidersRegistry(List<ResourceProvider> customProviders) {
    this.customProviders = List.copyOf(
        requireNonNull(customProviders, "customProviders must not be null"));
  }

  /**
   * Creates a {@link StrategyRegistry} combining default and custom providers.
   *
   * <p>The registry will include a default {@link ClasspathProvider} based on the given
   * {@code baseDirectories} and all previously registered custom providers. All registry keys are
   * normalized to lowercase.
   *
   * @param baseDirectories the base directories to use for the default classpath provider; must not
   *                        be {@code null}
   * @return a new immutable {@link StrategyRegistry} containing default and custom providers
   * @throws NullPointerException if {@code baseDirectories} is {@code null}
   */
  public StrategyRegistry<String, ResourceProvider> create(Collection<String> baseDirectories) {
    List<ResourceProvider> defaults = List.of(new ClasspathProvider(baseDirectories));

    List<ResourceProvider> merged = Stream.concat(defaults.stream(), customProviders.stream())
        .toList();

    return new StrategyRegistry<>(keyNormalizer, merged);
  }
}
