package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
   * @param input  the {@link InputStream} containing the configuration data, must not be null
   * @throws IOException          if reading or parsing the input stream fails
   * @throws NullPointerException if {@code mapper} or {@code input} are null
   */
  protected JacksonConfiguration(ObjectMapper mapper, InputStream input) throws IOException {
    super(new JacksonValueResolver(read(mapper, input)));
  }

  /**
   * Reads configuration data from the given input stream using the specified object mapper.
   *
   * @param mapper the {@link ObjectMapper} used for JSON parsing, must not be null
   * @param input  the {@link InputStream} containing the configuration data, must not be null
   * @return a {@link Map} containing the parsed configuration data
   * @throws IOException          if an error occurs while reading or parsing the input
   * @throws NullPointerException if {@code mapper} or {@code input} are null
   */
  private static Map<String, Object> read(ObjectMapper mapper, InputStream input)
      throws IOException {
    requireNonNull(mapper, "ObjectMapper must not be null");
    requireNonNull(input, "InputStream must not be null");
    return mapper.readValue(input, new TypeReference<>() {
    });
  }
}
