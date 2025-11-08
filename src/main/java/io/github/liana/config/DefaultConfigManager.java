package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
  private final ProvidersRegistry providers;
  private final LoadersRegistry loaders;
  private final JacksonMerger merger;
  private final JacksonInterpolator interpolator;

  /**
   * Creates a new {@code DefaultConfigManager} instance with a fresh internal
   * {@link LoadingCache}.
   *
   * <p>This is a convenience constructor for typical use cases where a custom cache
   * is not needed. It delegates to
   * {@link #DefaultConfigManager(LoadingCache, ProvidersRegistry, LoadersRegistry, JacksonMerger,
   * JacksonInterpolator)} using a new empty {@code LoadingCache}.</p>
   *
   * @param providers    supplies default and custom {@link ConfigProvider} implementations
   * @param loaders      supplies default and custom {@link ConfigLoader} implementations
   * @param merger       the {@link JacksonMerger} responsible for merging multiple configuration
   *                     maps
   * @param interpolator the {@link JacksonInterpolator} used to resolve placeholders within
   *                     configuration values
   */
  public DefaultConfigManager(ProvidersRegistry providers, LoadersRegistry loaders,
      JacksonMerger merger, JacksonInterpolator interpolator) {
    this(new LoadingCache<>(), providers, loaders, merger, interpolator);
  }

  /**
   * Creates a new {@code DefaultConfigManager} using a provided cache and dependency set.
   *
   * @param cache        the {@link LoadingCache} used to store loaded configuration entries
   * @param providers    supplies default and custom {@link ConfigProvider} implementations
   * @param loaders      supplies default and custom {@link ConfigLoader} implementations
   * @param merger       the {@link JacksonMerger} responsible for merging multiple configuration
   *                     maps
   * @param interpolator the {@link JacksonInterpolator} used to resolve placeholders within
   *                     configuration values
   */
  public DefaultConfigManager(LoadingCache<String, Map<String, Object>> cache,
      ProvidersRegistry providers, LoadersRegistry loaders, JacksonMerger merger,
      JacksonInterpolator interpolator) {
    this.cache = requireNonNull(cache);
    this.providers = requireNonNull(providers);
    this.loaders = requireNonNull(loaders);
    this.merger = requireNonNull(merger);
    this.interpolator = requireNonNull(interpolator);
  }

  /**
   * Loads and returns a {@link Configuration} for the given {@link ConfigResourceLocation}.
   *
   * <p>If the configuration has already been loaded and cached, the cached version is returned.
   * Otherwise, the configuration is loaded, merged, interpolated, and stored in cache.
   *
   * @param location the configuration resource location, must not be {@code null}
   * @return a {@link Configuration} for accessing the loaded configuration
   * @throws NullPointerException if {@code location} is {@code null}
   */
  @Override
  public Configuration load(ConfigResourceLocation location) {
    Set<String> baseDirectories = location.getBaseDirectories().toSet();
    var resource = new ClasspathResource(baseDirectories);
    StrategyRegistry<String, ConfigProvider> providersRegistry = providers.create(resource);
    StrategyRegistry<String, ConfigLoader> loadersRegistry = loaders.create();

    var provider = ConfigResourceProvider.of(providersRegistry);
    var loader = ConfigResourceLoader.of(loadersRegistry);
    var resourceExtensionResolver = new ResourceExtensionResolver(loadersRegistry.getAllKeys(),
        resource);
    var resourceNameValidator = new ResourceNameValidator(baseDirectories);
    var preparer = new ConfigResourcePreparer(location, resourceExtensionResolver,
        resourceNameValidator);
    var processor = new ConfigResourceProcessor(provider, loader, preparer);

    Map<String, Object> cachedConfig = cache.getOrCompute("ALL_CONFIG",
        () -> getConfig(processor, location));
    return new MapConfiguration(cachedConfig);
  }

  /**
   * Loads, merges, and interpolates configuration maps from the given location.
   *
   * @param location the configuration resource location
   * @return a merged and interpolated map of configuration values
   */
  private Map<String, Object> getConfig(ConfigResourceProcessor processor,
      ConfigResourceLocation location) {
    List<Map<String, Object>> configs = processor.load(location);
    Map<String, Object> merged = merger.merge(configs);
    return interpolator.interpolate(
        merged,
        location.getPlaceholder(),
        location.getVariables().toMap()
    );
  }
}
