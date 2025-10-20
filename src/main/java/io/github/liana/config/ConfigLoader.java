package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import java.util.Set;

/**
 * Loads configuration files from various sources and formats. Implementations handle specific file
 * formats (PROPERTIES, YAML, JSON, XML, etc.).
 */
public interface ConfigLoader extends Strategy<String>  {

  /**
   * Supported configuration extensions for this loader.
   */
  @Override
  Set<String> getKeys();

  /**
   * Loads and parses configuration from the given resource.
   *
   * @throws ConfigLoaderException if the resource is invalid or format is incorrect
   * @throws NullPointerException  if resource is null
   */
  Configuration load(ConfigResource resource);

  /**
   * Validates basic resource requirements. Default checks: non-null resource, input stream and
   * resource name.
   */
  default void validateResource(ConfigResource resource) {
    requireNonNull(resource, "ConfigResource must not be null");
    requireNonNull(resource.inputStream(), "inputStream must not be null");
    requireNonNull(resource.resourceName(), "resourceName must not be null");
  }
}
