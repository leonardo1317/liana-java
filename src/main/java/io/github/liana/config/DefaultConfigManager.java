package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link ConfigManager} responsible for loading, merging, and
 * interpolating configuration resources into a single unified configuration map.
 *
 * <p>This class coordinates the configuration loading process by delegating tasks to
 * specialized components:
 * <ul>
 *   <li>{@link ConfigResourceProcessor} — loads configuration data from the specified
 *       resource location.</li>
 *   <li>{@link JacksonMerger} — merges multiple configuration maps into one.</li>
 *   <li>{@link JacksonInterpolator} — replaces placeholders and variables within the
 *       merged configuration.</li>
 * </ul>
 *
 * <p>The final merged and interpolated configuration is cached to avoid redundant
 * loading operations.
 *
 * <p>Usage example:
 * <pre>{@code
 * ConfigManager manager = new DefaultConfigManager(cache, processor, merger, interpolator);
 * ConfigReader reader = manager.load(ConfigResourceLocation.of("classpath:config.yaml"));
 * }</pre>
 *
 * <p>This class is thread-safe if all injected dependencies are thread-safe.
 */
class DefaultConfigManager implements ConfigManager {

  private final LoadingCache<String, Map<String, Object>> cache;
  private final ConfigResourceProcessor resourceProcessor;
  private final JacksonMerger merger;
  private final JacksonInterpolator interpolator;

  /**
   * Creates a new {@code DefaultConfigManager} instance.
   *
   * @param cache             the cache instance used to store merged configurations, must not be
   *                          {@code null}
   * @param resourceProcessor the processor used to load configuration resources, must not be
   *                          {@code null}
   * @param merger            the component responsible for merging configuration maps, must not be
   *                          {@code null}
   * @param interpolator      the component responsible for interpolating placeholders and
   *                          variables, must not be {@code null}
   * @throws NullPointerException if any argument is {@code null}
   */
  public DefaultConfigManager(LoadingCache<String, Map<String, Object>> cache,
      ConfigResourceProcessor resourceProcessor,
      JacksonMerger merger,
      JacksonInterpolator interpolator) {
    this.cache = requireNonNull(cache);
    this.resourceProcessor = requireNonNull(resourceProcessor);
    this.merger = requireNonNull(merger);
    this.interpolator = requireNonNull(interpolator);
  }

  /**
   * Loads and returns a {@link ConfigReader} for the given {@link ConfigResourceLocation}.
   *
   * <p>If the configuration has already been loaded and cached, the cached version is returned.
   * Otherwise, the configuration is loaded, merged, interpolated, and stored in cache.
   *
   * @param location the configuration resource location, must not be {@code null}
   * @return a {@link ConfigReader} for accessing the loaded configuration
   * @throws NullPointerException if {@code location} is {@code null}
   */
  @Override
  public ConfigReader load(ConfigResourceLocation location) {
    requireNonNull(location, "ConfigResourceLocation cannot be null when loading configuration");
    Map<String, Object> cachedConfig = cache.getOrCompute("ALL_CONFIG", () -> getConfig(location));

    return new DefaultConfigReader(new MapConfiguration(cachedConfig));
  }

  /**
   * Loads, merges, and interpolates configuration maps from the given location.
   *
   * @param location the configuration resource location
   * @return a merged and interpolated map of configuration values
   */
  private Map<String, Object> getConfig(ConfigResourceLocation location) {
    List<Map<String, Object>> configs = resourceProcessor.load(location);
    Map<String, Object> merged = merger.merge(configs);
    return interpolator.interpolate(
        merged,
        location.getPlaceholder(),
        location.getVariables().toMap()
    );
  }
}
