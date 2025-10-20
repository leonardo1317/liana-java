package io.github.liana.config;

import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import io.github.liana.config.exception.ConfigProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Processes configuration resources by resolving, loading, and aggregating their contents into a
 * list of configuration maps.
 *
 * <p>This class coordinates the configuration loading workflow by delegating to two key
 * collaborators:
 *
 * <ul>
 *   <li>{@link ConfigResourceProvider} – resolves resource references into concrete
 *       {@link ConfigResource} instances.</li>
 *   <li>{@link ConfigResourceLoader} – loads the resolved resources and parses them into
 *       {@link Configuration} objects.</li>
 * </ul>
 *
 * <p>Each configuration resource is processed individually using
 * {@link #processSingleResource(ConfigResourceReference, ConfigLogger)}. Errors are logged but
 * do not interrupt the loading of other resources.
 *
 * <p>This processor supports detailed logging and performance measurement for each resource
 * load, which is helpful for debugging and performance analysis.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * ConfigResourceProvider provider = ConfigResourceProvider.of(registry);
 * ConfigResourceLoader loader = ConfigResourceLoader.of(loaderRegistry);
 * ConfigResourceProcessor processor = new ConfigResourceProcessor(provider, loader);
 *
 * ConfigResourceLocation location = ConfigResourceLocation.of("classpath:app.yaml");
 * List<Map<String, Object>> configs = processor.load(location);
 * }</pre>
 *
 * <p>This class is thread-safe if both {@link ConfigResourceProvider} and
 * {@link ConfigResourceLoader} are thread-safe.
 */
class ConfigResourceProcessor {

  private static final long NANOS_PER_MILLISECOND = 1_000_000L;
  private final ConfigResourceProvider provider;
  private final ConfigResourceLoader loader;

  /**
   * Creates a new {@code ConfigResourceProcessor} instance.
   *
   * @param provider the resource provider used to resolve configuration references, must not be
   *                 {@code null}
   * @param loader   the loader responsible for reading and parsing resolved resources, must not be
   *                 {@code null}
   * @throws NullPointerException if {@code provider} or {@code loader} is {@code null}
   */
  public ConfigResourceProcessor(ConfigResourceProvider provider, ConfigResourceLoader loader) {
    this.provider = requireNonNull(provider);
    this.loader = requireNonNull(loader);
  }

  /**
   * Loads and returns all configurations associated with the given location.
   *
   * <p>The processor resolves the resource references from the location, attempts to load each
   * resource, and returns a list of successfully loaded configuration maps.
   *
   * <p>Resources that cannot be resolved or loaded are skipped, and their errors are logged.
   *
   * @param location the configuration resource location, must not be {@code null}
   * @return an unmodifiable list of configuration maps representing the loaded resources
   * @throws NullPointerException if {@code location} is {@code null}
   */
  public List<Map<String, Object>> load(ConfigResourceLocation location) {
    requireNonNull(location);
    ConfigLogger log = ConsoleConfigLogger.getLogger(location.isVerboseLogging());
    log.debug(() -> "starting configuration load");
    ConfigResourcePreparer preparer = new ConfigResourcePreparer(location);
    List<ConfigResourceReference> references = preparer.prepare();
    List<Map<String, Object>> configs = new ArrayList<>(references.size());
    for (ConfigResourceReference reference : references) {
      processSingleResource(reference, log).ifPresent(configs::add);
    }

    log.info(() -> String.format(
        "configuration load completed: loaded=%d, failed=%d (total=%d)",
        configs.size(), references.size() - configs.size(), references.size()
    ));

    return Collections.unmodifiableList(configs);
  }

  /**
   * Processes a single configuration resource reference by resolving and loading it.
   *
   * <p>If the reference is invalid (e.g., null or missing provider/resource name), it is skipped.
   * Errors during provider resolution or resource loading are logged but do not propagate.
   *
   * @param reference the configuration resource reference to process
   * @param log       the logger used to record diagnostic messages
   * @return an {@link Optional} containing the loaded configuration map if successful, or an empty
   * {@code Optional} if the resource could not be processed
   */
  private Optional<Map<String, Object>> processSingleResource(
      ConfigResourceReference reference, ConfigLogger log) {

    if (isNull(reference) || isBlank(reference.provider()) || isBlank(reference.resourceName())) {
      log.debug(() -> "skipping empty provider or resource name");
      return Optional.empty();
    }

    try {
      long startTime = System.nanoTime();
      ConfigResource resource = provider.resolve(reference);
      Configuration configuration = loader.loadFromResource(resource);
      Map<String, Object> config = configuration.getRootAsMap();

      long durationMs = (System.nanoTime() - startTime) / NANOS_PER_MILLISECOND;
      log.debug(() -> String.format(
          "loaded %s with %d entries in %dms", reference.resourceName(), config.size(), durationMs
      ));

      return Optional.of(config);

    } catch (ConfigProviderException e) {
      log.error(() -> "failed to obtain provider for " + reference.provider(), e);
    } catch (ConfigLoaderException e) {
      log.error(() -> "failed to load configuration from " + reference.resourceName(), e);
    } catch (Exception e) {
      log.error(() -> "unexpected error while processing " + reference.resourceName(), e);
    }

    return Optional.empty();
  }
}
