package io.github.liana.config;

import static io.github.liana.config.ConfigFileFormat.PROPERTIES;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Implementation of {@link ConfigLoader} for Properties configuration files.
 */
final class PropertiesLoader implements ConfigLoader {

  private final ConfigParser configParser;

  /**
   * Creates a new {@code PropertiesLoader} with the specified configuration parser.
   *
   * <p>The provided parser will be used to transform Properties resources into
   * {@link Configuration} instances.
   *
   * @param configParser the parser responsible for interpreting Properties input streams (must not
   *                     be null)
   * @throws NullPointerException if {@code configParser} is {@code null}
   */
  PropertiesLoader(ConfigParser configParser) {
    this.configParser = requireNonNull(configParser, "configParser must not be null");
  }

  /**
   * Gets the configuration file format supported by this loader.
   *
   * <p>This implementation specifically returns the Properties format.
   *
   * @return an immutable {@link Set} of supported file extensions
   * @see ConfigFileFormat#PROPERTIES
   */
  @Override
  public Set<String> getKeys() {
    return PROPERTIES.getExtensions();
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
    try (InputStream input = resource.inputStream()) {
      return configParser.parse(input);
    } catch (IOException e) {
      throw new ConfigLoaderException(
          "Error loading Properties config from " + resource.resourceName(), e);
    }
  }
}
