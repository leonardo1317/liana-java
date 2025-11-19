package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import io.github.liana.internal.FilenameUtils;
import java.util.Locale;

/**
 * Resolves {@link ConfigLoader} instances based on configuration file types.
 *
 * <p>This class delegates loader selection to a set of registered
 * {@link ConfigLoader} strategies, using the file extension of the provided
 * resource name. It does not perform the actual loading; it only chooses
 * the appropriate loader.</p>
 *
 * <p>Instances of this class are immutable and thread-safe.</p>
 */
final class ConfigLoaderResolver {

  private final StrategyRegistry<String, ConfigLoader> strategies;

  /**
   * Creates a new {@code ConfigLoaderResolver} with the given strategy registry.
   *
   * @param strategies the registry containing {@link ConfigLoader} implementations
   *                   keyed by file extension; must not be {@code null}
   */
  public ConfigLoaderResolver(StrategyRegistry<String, ConfigLoader> strategies) {
    this.strategies = requireNonNull(strategies, "strategies must not be null");
  }

  /**
   * Resolves a {@link ConfigLoader} based on the file extension of the given resource name.
   *
   * <p>The file extension determines which {@link ConfigLoader} strategy will be used.
   * If no loader is registered for the given extension, a {@link ConfigLoaderException}
   * is thrown.</p>
   *
   * @param resourceName the name of the configuration resource; must not be {@code null} or blank
   * @return the matching {@link ConfigLoader}
   * @throws ConfigLoaderException if the file type is unsupported or no loader is registered
   */
  public ConfigLoader resolve(String resourceName) {
    requireNonBlank(resourceName,
        "resource name ust not be null or blank");
    String fileExtension = FilenameUtils.getExtension(resourceName).toLowerCase(Locale.ROOT);

    return strategies.get(fileExtension)
        .orElseThrow(() -> new ConfigLoaderException("no config loader registered for file type: " + resourceName));
  }
}
