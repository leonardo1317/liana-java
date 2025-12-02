package io.github.liana.config.loaders;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.ResourceParser;
import io.github.liana.config.core.ResourceStream;
import io.github.liana.config.core.exception.ResourceLoaderException;
import io.github.liana.config.spi.ResourceLoader;
import java.io.IOException;
import java.util.Set;

/**
 * Abstract base class for configuration loaders.
 *
 * <p>Provides common functionality for loading configuration resources from an input stream
 * using a {@link ResourceParser}. Concrete subclasses must specify the supported file extensions.
 *
 * <p>This class centralizes resource validation and error handling. Subclasses only need
 * to implement {@link #getSupportedExtensions()} to indicate which file extensions they can load.
 *
 * <p>Instances are immutable. Thread-safety depends on the provided {@link ResourceParser}.
 */
public abstract class AbstractResourceLoader implements ResourceLoader {

  private final ResourceParser parser;

  /**
   * Constructs a new loader with the given parser.
   *
   * @param parser the {@link ResourceParser} to use for parsing input streams; must not be
   *               {@code null}
   * @throws NullPointerException if parser is {@code null}
   */
  protected AbstractResourceLoader(ResourceParser parser) {
    this.parser = requireNonNull(parser);
  }

  /**
   * Returns the set of supported file extensions for this loader.
   *
   * @return an immutable set of file extensions (e.g., "yaml", "yml")
   */
  protected abstract Set<String> getSupportedExtensions();

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getKeys() {
    return getSupportedExtensions();
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation validates the resource and parses its content using the configured
   * {@link ResourceParser}. If an I/O error occurs, a {@link ResourceLoaderException} is thrown.
   *
   * @throws ResourceLoaderException if reading or parsing the file format fails
   */
  @Override
  public Configuration load(ResourceStream resource) {
    validateResource(resource);
    try {
      return parser.parse(resource.stream());
    } catch (IOException e) {
      throw new ResourceLoaderException("Error loading config from " + resource.name(), e);
    }
  }
}
