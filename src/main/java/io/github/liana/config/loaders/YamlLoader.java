package io.github.liana.config.loaders;

import static io.github.liana.config.core.FileFormat.YAML;

import io.github.liana.config.core.ResourceParser;
import io.github.liana.config.spi.ResourceLoader;
import java.util.Set;

/**
 * Loads YAML configuration resources.
 *
 * <p>This loader handles YAML files using a provided {@link ResourceParser}. It implements
 * {@link ResourceLoader} and supports the standard YAML extensions ("yaml" and "yml").
 *
 * <p>Instances are immutable. Thread-safety depends on the provided {@link ResourceParser}.
 * This loader focuses exclusively on parsing YAML; it does not merge or interpolate
 * configurations.
 */
public class YamlLoader extends AbstractResourceLoader {


  /**
   * Constructs a new loader with the given parser.
   *
   * @param parser the {@link ResourceParser} to use for parsing input streams; must not be
   *               {@code null}
   * @throws NullPointerException if parser is {@code null}
   */
  public YamlLoader(ResourceParser parser) {
    super(parser);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns the set of file extensions this loader supports, as defined by the YAML
   * format.
   */
  @Override
  protected Set<String> getSupportedExtensions() {
    return YAML.getExtensions();
  }
}
