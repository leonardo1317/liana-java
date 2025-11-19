package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigProviderException;

/**
 * Resolves {@link ConfigProvider} instances based on a provider identifier.
 *
 * <p>This class uses a {@link StrategyRegistry} to look up the appropriate
 * {@link ConfigProvider} registered under a given provider name. If the identifier does not match
 * any registered provider, a {@link ConfigProviderException} is thrown.</p>
 *
 * <p>This class is immutable and thread-safe.</p>
 */
class ConfigProviderResolver {

  private final StrategyRegistry<String, ConfigProvider> strategies;

  /**
   * Creates a new {@code ConfigProviderResolver} backed by the given strategy registry.
   *
   * @param strategies the registry containing {@link ConfigProvider} implementations keyed by
   *                   provider name; must not be {@code null}
   * @throws NullPointerException if {@code strategies} is {@code null}
   */
  public ConfigProviderResolver(StrategyRegistry<String, ConfigProvider> strategies) {
    this.strategies = requireNonNull(strategies, "strategies must not be null");
  }

  /**
   * Resolves a {@link ConfigProvider} by its provider identifier.
   *
   * <p>The identifier is matched against the keys registered in the underlying
   * {@link StrategyRegistry}. If no provider matches, a {@link ConfigProviderException} is
   * thrown.</p>
   *
   * @param provider the provider identifier; must not be {@code null} or blank
   * @return the matching {@link ConfigProvider}
   * @throws IllegalArgumentException if {@code providerIdentifier} is {@code null} or blank
   * @throws ConfigProviderException  if no provider is registered under the identifier
   */
  public ConfigProvider resolve(String provider) {
    requireNonBlank(provider, "provider identifier must not be null or blank");

    return strategies.get(provider)
        .orElseThrow(
            () -> new ConfigProviderException(
                "no ConfigProvider is registered for identifier: " + provider));
  }
}
