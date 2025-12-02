package io.github.liana.config.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;

/**
 * Jackson-based implementation of {@link AbstractConfiguration}.
 *
 * <p>This class reads configuration data from an {@link InputStream} using a provided
 * {@link ObjectMapper}. The parsed data is converted into a {@link Map} and passed to the parent
 * {@link AbstractConfiguration}.
 *
 * <p>Instances of this class are immutable once constructed.
 *
 * <p><b>Thread Safety:</b> Instances are thread-safe as long as the provided {@link ObjectMapper}
 * is thread-safe and the input stream is not modified concurrently.
 *
 * <p><b>Responsibilities:</b> Parsing the input stream into a map for configuration retrieval.
 * Does not perform merging or dynamic updates of the configuration after construction.
 */
public class JacksonConfiguration extends AbstractConfiguration {

  /**
   * Creates a new {@code JacksonConfiguration} by reading configuration data from the given
   * {@link InputStream}.
   *
   * @param mapper      the {@link ObjectMapper} used to parse the input, must not be {@code null}
   * @param inputStream the {@link InputStream} containing the configuration data, must not be
   *                    {@code null}
   * @throws NullPointerException if {@code mapper} or {@code inputStream} are {@code null}
   */
  protected JacksonConfiguration(ObjectMapper mapper, InputStream inputStream) {
    super(new JacksonValueResolver(mapper, inputStream));
  }
}
