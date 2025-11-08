package io.github.liana.config;

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
 * <p>Example usage:
 *
 * <pre>{@code
 * ObjectMapper mapper = new ObjectMapper();
 * try (InputStream input = Files.newInputStream(Path.of("config.json"))) {
 *   JacksonConfiguration config = new JacksonConfiguration(mapper, input);
 *   Optional<String> value = config.get("some.key", String.class);
 * }
 * }</pre>
 *
 * <p>Instances of this class are immutable once constructed.
 */
public class JacksonConfiguration extends AbstractConfiguration {

  /**
   * Creates a new {@code JacksonConfiguration} by reading configuration data from the given
   * {@link InputStream}.
   *
   * @param mapper the {@link ObjectMapper} used to parse the input, must not be null
   * @param inputStream  the {@link InputStream} containing the configuration data, must not be null
   * @throws NullPointerException if {@code mapper} or {@code inputStream} are null
   */
  protected JacksonConfiguration(ObjectMapper mapper, InputStream inputStream) {
    super(new JacksonValueResolver(mapper, inputStream));
  }
}
