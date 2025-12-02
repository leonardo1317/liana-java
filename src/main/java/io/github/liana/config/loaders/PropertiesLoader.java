package io.github.liana.config.loaders;

import static io.github.liana.config.core.FileFormat.PROPERTIES;

import io.github.liana.config.core.ResourceParser;
import io.github.liana.config.spi.ResourceLoader;
import java.util.Set;

/**
 * Loads PROPERTIES configuration resources.
 *
 * <p>This loader handles PROPERTIES files using a provided {@link ResourceParser}. It implements
 * {@link ResourceLoader} and supports the standard PROPERTIES extension ("properties").
 *
 * <p>Instances are immutable. Thread-safety depends on the provided {@link ResourceParser}.
 * This loader focuses exclusively on parsing PROPERTIES; it does not merge or interpolate
 * configurations.
 */
public class PropertiesLoader extends AbstractResourceLoader {

  /**
   * Constructs a new loader with the given parser.
   *
   * @param parser the {@link ResourceParser} to use for parsing input streams; must not be
   *               {@code null}
   * @throws NullPointerException if parser is {@code null}
   */
  public PropertiesLoader(ResourceParser parser) {
    super(parser);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns the set of file extensions this loader supports, as defined by the PROPERTIES
   * format.
   */
  @Override
  protected Set<String> getSupportedExtensions() {
    return PROPERTIES.getExtensions();
  }
}
