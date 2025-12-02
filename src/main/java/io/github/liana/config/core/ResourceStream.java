package io.github.liana.config.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a resource that exposes its content through an {@link InputStream}.
 *
 * <p>This interface is part of the public SPI. Implementations wrap the result of a
 * successfully resolved resource provided by a {@code ResourceProvider}. The stream returned by
 * {@link #stream()} must be readable and non-null.
 *
 * <p>Instances may be immutable or stateful depending on the implementation. Thread-safety
 * is not guaranteed unless explicitly documented by the implementer.
 *
 * <p>Callers are responsible for closing the resource. Closing this handle closes the
 * underlying stream.
 */
public interface ResourceStream extends AutoCloseable {

  /**
   * Returns the logical name of the resource. Typical use cases include file names, classpath
   * resource names, or virtual identifiers used by custom providers.
   *
   * @return the non-null resource name
   */
  String name();

  /**
   * Returns the {@link InputStream} that provides access to the resource content.
   *
   * <p>The stream is guaranteed to be non-null and open. Implementations may return the
   * same instance on repeated calls or create new streams depending on their design.
   *
   * @return the resource content stream
   */
  InputStream stream();

  /**
   * Closes the underlying stream. Further calls to {@link #stream()} after closing may result in
   * undefined behavior depending on the implementation.
   *
   * @throws IOException if closing the stream fails
   */
  @Override
  void close() throws IOException;

}
