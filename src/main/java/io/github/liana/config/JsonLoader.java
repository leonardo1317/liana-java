package io.github.liana.config;

import static io.github.liana.config.ConfigFileFormat.JSON;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Implementation of {@link ConfigLoader} for JSON configuration files.
 */
final class JsonLoader implements ConfigLoader {

  private final ConfigParser configParser;

  /**
   * Creates a new {@code JsonLoader} with the specified configuration parser.
   *
   * <p>The provided parser will be used to transform JSON resources into
   * {@link Configuration} instances.
   *
   * @param configParser the parser responsible for interpreting JSON input streams (must not be
   *                     null)
   * @throws NullPointerException if {@code configParser} is {@code null}
   */
  JsonLoader(ConfigParser configParser) {
    this.configParser = requireNonNull(configParser, "configParser must not be null");
  }

  /**
   * Gets the configuration file format supported by this loader.
   *
   * <p>This implementation specifically returns the JSON format.
   *
   * @return an immutable {@link Set} of supported file extensions
   * @see ConfigFileFormat#JSON
   */
  @Override
  public Set<String> getKeys() {
    return JSON.getExtensions();
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
    try (InputStream input = resource.inputStream()) {
      return configParser.parse(input);
    } catch (IOException e) {
      throw new ConfigLoaderException(
          "Error loading Json config from " + resource.resourceName(), e);
    }
  }
}
