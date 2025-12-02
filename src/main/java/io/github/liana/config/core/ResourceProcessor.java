package io.github.liana.config.core;

import static io.github.liana.config.internal.StringUtils.isBlank;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.exception.ResourceLoaderException;
import io.github.liana.config.core.exception.ResourceProviderException;
import io.github.liana.config.core.logging.ConsoleLogger;
import io.github.liana.config.core.logging.Logger;
import io.github.liana.config.internal.FilenameUtils;
import io.github.liana.config.internal.ImmutableConfigSet;
import io.github.liana.config.spi.ResourceLoader;
import io.github.liana.config.spi.ResourceProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Processes configuration resources using registered providers and loaders.
 *
 * <p>This class coordinates the resource preparation phase, the resolution of providers and
 * loaders, and the final loading of configuration maps. It acts as an internal orchestration
 * component within the configuration-loading module and is not intended as a public API or SPI.
 *
 * <p><strong>Responsibilities:</strong>
 * <ul>
 *   <li>Invokes {@link ResourcePreparer} to obtain the list of resources to process.</li>
 *   <li>Resolves the appropriate {@link ResourceProvider} for each resource.</li>
 *   <li>Resolves the appropriate {@link ResourceLoader} based on file extension.</li>
 *   <li>Loads each resource into a {@code Map<String,Object>} representation.</li>
 *   <li>Applies caching for provider and loader strategy resolvers.</li>
 * </ul>
 *
 * <p><strong>Limitations:</strong>
 * <ul>
 *   <li>Errors in individual resources do not stop the pipeline; failing resources are logged
 *       and skipped.</li>
 *   <li>The class does not validate the semantic correctness of the configuration content.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong></p>
 * <ul>
 *   <li>This class is <em>effectively immutable</em> after construction.</li>
 *   <li>It holds thread-safe caches via {@link LoadingCache}, but the class itself is not
 *       explicitly synchronized.</li>
 *   <li>Instances are safe for concurrent use only if all injected components are thread-safe.</li>
 * </ul>
 */
public class ResourceProcessor {

  private static final String LOADER_CACHE_KEY = "default";
  private final ProvidersRegistry providers;
  private final LoadersRegistry loaders;
  private final LoadingCache<ImmutableConfigSet, StrategyResolver<String, ResourceProvider>> providerCache;
  private final LoadingCache<String, StrategyResolver<String, ResourceLoader>> loaderCache;
  private final ResourcePreparer resourcePreparer;

  /**
   * Creates a {@code ResourceProcessor} with default caches.
   *
   * @param providers        the registry supplying available {@link ResourceProvider} instances; must
   *                         not be null
   * @param loaders          the registry supplying available {@link ResourceLoader} instances; must
   *                         not be null
   * @param resourcePreparer the preparer used to obtain resource identifiers; must not be null
   * @throws NullPointerException if any argument is {@code null}
   */
  public ResourceProcessor(
      ProvidersRegistry providers,
      LoadersRegistry loaders,
      ResourcePreparer resourcePreparer
  ) {
    this(providers, loaders, resourcePreparer, new LoadingCache<>(), new LoadingCache<>());
  }

  /**
   * Creates a {@code ResourceProcessor} with explicit caches.
   *
   * @param providers        the provider registry; must not be null
   * @param loaders          the loader registry; must not be null
   * @param resourcePreparer the preparer for resource identifiers; must not be null
   * @param providerCache    cache for provider strategy resolvers; must not be null
   * @param loaderCache      cache for loader strategy resolvers; must not be null
   * @throws NullPointerException if any parameter is {@code null}
   */
  public ResourceProcessor(
      ProvidersRegistry providers, LoadersRegistry loaders,
      ResourcePreparer resourcePreparer,
      LoadingCache<ImmutableConfigSet, StrategyResolver<String, ResourceProvider>> providerCache,
      LoadingCache<String, StrategyResolver<String, ResourceLoader>> loaderCache
  ) {
    this.providers = requireNonNull(providers);
    this.loaders = requireNonNull(loaders);
    this.resourcePreparer = requireNonNull(resourcePreparer);
    this.providerCache = requireNonNull(providerCache);
    this.loaderCache = requireNonNull(loaderCache);
  }

  /**
   * Loads all prepared configuration resources using registered providers and loaders.
   *
   * <p>The method logs progress, collects successfully loaded configurations, and skips
   * resources that fail to load.
   *
   * @param location the configuration location providing base directories and logging options; must
   *                 not be null
   * @return an unmodifiable list of configuration maps; never null
   * @throws NullPointerException if {@code location} is {@code null}
   */
  public List<Map<String, Object>> load(ResourceLocation location) {
    requireNonNull(location);
    Logger log = ConsoleLogger.getLogger(location.verboseLogging());
    log.debug(() -> "starting configuration load");

    List<ResourceIdentifier> identifiers = resourcePreparer.prepare();
    var configs = new ArrayList<Map<String, Object>>(identifiers.size());
    for (ResourceIdentifier identifier : identifiers) {
      processSingleResource(identifier, log, location.baseDirectories()).ifPresent(configs::add);
    }

    log.info(() -> String.format(
        "configuration load completed: loaded=%d, failed=%d (total=%d)",
        configs.size(), identifiers.size() - configs.size(), identifiers.size()
    ));

    return Collections.unmodifiableList(configs);
  }

  private Optional<Map<String, Object>> processSingleResource(
      ResourceIdentifier identifier, Logger log, ImmutableConfigSet dirs) {

    if (isNull(identifier) || isBlank(identifier.provider()) || isBlank(identifier.resourceName())) {
      log.debug(() -> "skipping empty provider or resource name");
      return Optional.empty();
    }

    try {
      long start = System.nanoTime();
      StrategyResolver<String, ResourceProvider> providerResolver = getProviderResolver(dirs);
      ResourceProvider resourceProvider = providerResolver.resolve(identifier.provider());

      try (ResourceStream resource = resourceProvider.resolveResource(identifier)) {
        String fileExtension = FilenameUtils.getExtension(resource.name())
            .toLowerCase(Locale.ROOT);
        StrategyResolver<String, ResourceLoader> loaderResolver = getLoaderResolver();
        ResourceLoader resourceLoader = loaderResolver.resolve(fileExtension);
        Configuration configuration = resourceLoader.load(resource);
        Map<String, Object> config = configuration.getRootAsMap();

        long durationNs = System.nanoTime() - start;
        long durationMs = TimeUnit.NANOSECONDS.toMillis(durationNs);
        log.debug(() -> String.format(
            "loaded %s with %d entries in %dms", identifier.resourceName(), config.size(), durationMs
        ));

        return Optional.of(config);
      }

    } catch (ResourceProviderException e) {
      log.error(() -> "failed to obtain provider for " + identifier.provider(), e);
    } catch (ResourceLoaderException e) {
      log.error(() -> "failed to load configuration from " + identifier.resourceName(), e);
    } catch (Exception e) {
      log.error(() -> "unexpected error while processing " + identifier.resourceName(), e);
    }

    return Optional.empty();
  }

  private StrategyResolver<String, ResourceProvider> getProviderResolver(ImmutableConfigSet dirs) {
    return providerCache.getOrCompute(dirs, () -> new StrategyResolver<>(
        providers.create(dirs.toSet()),
        key -> new ResourceProviderException("No provider registered for key: " + key)
    ));
  }

  private StrategyResolver<String, ResourceLoader> getLoaderResolver() {
    return loaderCache.getOrCompute(LOADER_CACHE_KEY, () -> new StrategyResolver<>(
        loaders.create(),
        key -> new ResourceLoaderException("No loader registered for extension: " + key)
    ));
  }
}
