package io.github.liana.config;

import static io.github.liana.config.ConfigFileFormat.PROPERTIES;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link ConfigLoader} for Properties configuration files.
 */
final class PropertiesConfigLoader implements ConfigLoader {

  /**
   * Gets the configuration file format supported by this loader.
   *
   * <p>This implementation specifically returns the Properties format.
   *
   * @return The {@link ConfigFileFormat#PROPERTIES} constant representing the Properties format
   * @see ConfigFileFormat#PROPERTIES
   */
  @Override
  public ConfigFileFormat getFileFormat() {
    return PROPERTIES;
  }

  /**
   * Loads and parses a Properties configuration resource.
   *
   * @param resource The configuration resource to load (must not be null).
   * @return the parsed {@link Configuration} from the Properties resource.
   * @throws NullPointerException  If {@code resource} or any of its required fields (input stream,
   *                               resource name) are null.
   * @throws ConfigLoaderException if the resource is invalid or the Properties is malformed.
   */
  @Override
  public Configuration load(ConfigResource resource) {
    validateResource(resource);
    try (InputStream input = resource.getInputStream()) {
      return new JacksonConfiguration(ObjectMapperProvider.getPropertiesInstance(), input);
    } catch (IOException e) {
      throw new ConfigLoaderException(
          "Error loading Properties config from " + resource.getResourceName(), e);
    }
  }
}
