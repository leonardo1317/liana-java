package io.github.liana.config.core;

import io.github.liana.config.api.Configuration;
import java.io.IOException;
import java.io.InputStream;

/**
 * Strategy interface for parsing configuration data from an {@link InputStream}.
 *
 * <p>Implementations convert the raw input stream into a {@link Configuration} object, providing
 * a consistent way to access configuration properties across different formats (JSON, YAML, etc.).
 *
 * <p>Implementations are responsible for handling I/O errors and ensuring thread-safety if
 * instances are shared between threads.
 */
public interface ResourceParser {

  /**
   * Parses configuration data from the provided input stream.
   *
   * <p>Each call produces a new {@link Configuration} object representing the parsed data.
   *
   * @param inputStream the input stream containing configuration data; must not be null
   * @return a {@link Configuration} representing the parsed data
   * @throws IOException          if reading from the stream fails
   * @throws NullPointerException if {@code inputStream} is null
   */
  Configuration parse(InputStream inputStream) throws IOException;
}
