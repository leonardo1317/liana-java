package io.github.liana.config.loaders;

import static io.github.liana.config.core.FileFormat.XML;

import io.github.liana.config.core.ResourceParser;
import io.github.liana.config.spi.ResourceLoader;
import java.util.Set;

/**
 * Loads XML configuration resources.
 *
 * <p>This loader handles XML files using a provided {@link ResourceParser}. It implements
 * {@link ResourceLoader} and supports the standard XML extension ("xml").
 *
 * <p>Instances are immutable. Thread-safety depends on the provided {@link ResourceParser}.
 * This loader focuses exclusively on parsing XML; it does not merge or interpolate configurations.
 */
public class XmlLoader extends AbstractResourceLoader {

  /**
   * Constructs a new loader with the given parser.
   *
   * @param parser the {@link ResourceParser} to use for parsing input streams; must not be
   *               {@code null}
   * @throws NullPointerException if parser is {@code null}
   */
  public XmlLoader(ResourceParser parser) {
    super(parser);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns the set of file extensions this loader supports, as defined by the XML
   * format.
   */
  @Override
  protected Set<String> getSupportedExtensions() {
    return XML.getExtensions();
  }
}
