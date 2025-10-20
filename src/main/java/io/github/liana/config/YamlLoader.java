package io.github.liana.config;

import static io.github.liana.config.ConfigFileFormat.YAML;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Implementation of {@link ConfigLoader} for YAML configuration files.
 */
final class YamlLoader implements ConfigLoader {

  private final ConfigParser configParser;

  /**
   * Creates a new {@code YamlLoader} with the specified configuration parser.
   *
   * <p>The provided parser will be used to transform YAML resources into
   * {@link Configuration} instances.
   *
   * @param configParser the parser responsible for interpreting YAML input streams (must not be
   *                     null)
   * @throws NullPointerException if {@code configParser} is {@code null}
   */
  YamlLoader(ConfigParser configParser) {
    this.configParser = requireNonNull(configParser, "configParser must not be null");
  }

  /**
   * Gets the configuration file format supported by this loader.
   *
   * <p>This implementation specifically returns the YAML format, which supports both ".yaml" and
   * ".yml" file extensions.
   *
   * @return an immutable {@link Set} of supported file extensions
   * @see ConfigFileFormat#YAML
   */
  @Override
  public Set<String> getKeys() {
    return YAML.getExtensions();
  }

  /**
   * Loads and parses an YAML configuration resource.
   *
   * @param resource The configuration resource to load (must not be null).
   * @return the parsed {@link Configuration} from the YAML resource.
   * @throws NullPointerException  If {@code resource} or any of its required fields (input stream,
   *                               resource name) are null.
   * @throws ConfigLoaderException if the resource is invalid or the YAML is malformed.
   */
  @Override
  public Configuration load(ConfigResource resource) {
    validateResource(resource);
    try (InputStream input = resource.inputStream()) {
      return configParser.parse(input);
    } catch (IOException e) {
      throw new ConfigLoaderException(
          "Error loading Yaml config from " + resource.resourceName(), e);
    }
  }
}
