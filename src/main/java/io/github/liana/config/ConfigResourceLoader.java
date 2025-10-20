package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import io.github.liana.internal.FilenameUtils;
import java.util.Locale;
import java.util.Map;

/**
 * Loads configuration objects from different types of resources.
 *
 * <p>This class delegates the loading process to a set of registered
 * {@link ConfigLoader} strategies, based on the file extension of the resource. It provides
 * convenience methods to load configurations either from a {@link ConfigResource} or directly from
 * a {@link Map}.
 *
 * <p>Instances of this class are immutable and thread-safe.
 */
final class ConfigResourceLoader {

  private final StrategyRegistry<String, ConfigLoader> strategies;

  /**
   * Creates a new {@code ConfigResourceLoader} with the given strategy registry.
   *
   * @param strategies the registry containing {@link ConfigLoader} implementations keyed by file
   *                   extension, must not be {@code null}
   */
  private ConfigResourceLoader(StrategyRegistry<String, ConfigLoader> strategies) {
    this.strategies = requireNonNull(strategies, "strategies must not be null");
  }

  /**
   * Factory method to create a {@code ConfigResourceLoader}.
   *
   * @param strategies the registry containing {@link ConfigLoader} implementations keyed by file
   *                   extension, must not be {@code null}
   * @return a new {@code ConfigResourceLoader} instance
   */
  public static ConfigResourceLoader of(StrategyRegistry<String, ConfigLoader> strategies) {
    return new ConfigResourceLoader(strategies);
  }

  /**
   * Loads a {@link Configuration} from a {@link ConfigResource}.
   *
   * <p>The file extension of the resource name determines which {@link ConfigLoader}
   * will be used. If no matching strategy is found, a {@link ConfigLoaderException} is thrown.
   *
   * @param resource the configuration resource to load, must not be {@code null}
   * @return the loaded {@link Configuration}
   * @throws ConfigLoaderException if the file type is unsupported or if an error occurs
   */
  public Configuration loadFromResource(ConfigResource resource) {
    requireNonNull(resource, "resource cannot be null to create a configuration");
    String resourceName = requireNonBlank(resource.resourceName(),
        "resourceName cannot be null or blank to create a configuration");
    String fileExtension = FilenameUtils.getExtension(resourceName).toLowerCase(Locale.ROOT);

    return strategies.get(fileExtension)
        .map(loader -> load(loader, resource))
        .orElseThrow(() -> new ConfigLoaderException("unsupported config file " + resourceName));
  }

  /**
   * Executes the given {@link ConfigLoader} to load a configuration from a resource.
   *
   * @param loader   the loader to use, must not be {@code null}
   * @param resource the configuration resource, must not be {@code null}
   * @return the loaded {@link Configuration}
   * @throws ConfigLoaderException if the loader fails or an unexpected error occurs
   */
  private Configuration load(ConfigLoader loader, ConfigResource resource) {
    try {
      return loader.load(resource);
    } catch (ConfigLoaderException e) {
      throw e;
    } catch (Exception e) {
      throw new ConfigLoaderException(
          "unexpected error while loading configuration with " + loader.getClass().getSimpleName(),
          e);
    }
  }
}
