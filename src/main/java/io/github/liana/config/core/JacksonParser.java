package io.github.liana.config.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.api.Configuration;
import java.io.IOException;
import java.io.InputStream;

/**
 * Jackson-based implementation of {@link ResourceParser}.
 *
 * <p>This class uses a provided {@link ObjectMapper} to read JSON configuration data from an
 * {@link InputStream} and produce a {@link JacksonConfiguration} instance.
 *
 * <p>Instances are immutable and thread-safe as long as the provided {@link ObjectMapper} is
 * thread-safe.
 *
 * <p>Intended for internal use where JSON configuration parsing is required.
 */
public final class JacksonParser extends AbstractJacksonComponent implements ResourceParser {

  /**
   * Creates a new {@code JacksonParser} with the given {@link ObjectMapper}.
   *
   * @param mapper the object mapper used for JSON conversions; must not be null
   * @throws NullPointerException if {@code mapper} is null
   */
  public JacksonParser(ObjectMapper mapper) {
    super(mapper);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation parses JSON from the provided {@link InputStream} using the
   * {@link ObjectMapper} supplied at construction. Each call returns a new
   * {@link JacksonConfiguration} instance.
   *
   * <p>The input stream must not be {@code null}.
   */
  @Override
  public Configuration parse(InputStream inputStream) throws IOException {
    return new JacksonConfiguration(mapper, inputStream);
  }
}
