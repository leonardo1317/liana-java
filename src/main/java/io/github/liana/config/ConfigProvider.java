package io.github.liana.config;

import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigProviderException;
import java.util.Set;

/**
 * Provides configuration resources from different sources. Implementations handle specific
 * locations like filesystem, classpath or remote URLs.
 */
public interface ConfigProvider extends Strategy<String> {

  /**
   * Unique identifier for this provider (e.g., "filesystem", "classpath").
   */
  @Override
  Set<String> getKeys();

  /**
   * Resolves a configuration resource into a loadable format.
   *
   * @throws ConfigProviderException if resource can't be resolved
   * @throws NullPointerException    if resource is null
   */
  ConfigResource resolveResource(ConfigResourceReference resource);

  /**
   * Validates basic resource requirements. Default checks: non-null resource and resource name.
   */
  default void validateResource(ConfigResourceReference resource) {
    requireNonNull(resource, "ConfigResourceReference must not be null");
    requireNonBlank(resource.resourceName(), "ResourceNames must not be null");
  }
}
