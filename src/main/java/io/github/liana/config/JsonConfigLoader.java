package io.github.liana.config;

import static io.github.liana.config.ConfigFileFormat.JSON;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link ConfigLoader} for JSON configuration files.
 */
final class JsonConfigLoader implements ConfigLoader {

  /**
   * Gets the configuration file format supported by this loader.
   *
   * <p>This implementation specifically returns the JSON format.
   *
   * @return The {@link ConfigFileFormat#JSON} constant representing the JSON format
   * @see ConfigFileFormat#JSON
   */
  @Override
  public ConfigFileFormat getFileFormat() {
    return JSON;
  }

  /**
   * Loads and parses an JSON configuration resource.
   *
   * @param resource The configuration resource to load (must not be null).
   * @return the parsed {@link Configuration} from the JSON resource.
   * @throws NullPointerException  If {@code resource} or any of its required fields (input stream,
   *                               resource name) are null.
   * @throws ConfigLoaderException if the resource is invalid or the JSON is malformed.
   */
  @Override
  public Configuration load(ConfigResource resource) {
    validateResource(resource);
    try (InputStream input = resource.getInputStream()) {
      return new JacksonConfiguration(ObjectMapperProvider.getJsonInstance(), input);
    } catch (IOException e) {
      throw new ConfigLoaderException(
          "Error loading Json config from " + resource.getResourceName(), e);
    }
  }
}
