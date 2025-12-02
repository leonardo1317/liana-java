package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Default implementation of {@link ResourceStream} representing a configuration resource.
 *
 * <p>Instances are immutable and thread-safe with respect to their structural properties
 * (name and reference to the stream). The underlying {@link InputStream} itself may not be
 * thread-safe.
 *
 * <p>This class manages the lifecycle of the resource's {@link InputStream}. Calling
 * {@link #close()} closes the stream and releases system resources. Framework-provided loaders
 * ensure proper closure, but custom implementations must handle it manually to avoid leaks.
 *
 * <p>Typically used to pass configuration data from various sources
 * (filesystem, classpath, network, etc.) to configuration loaders or managers.
 *
 * <p>Implementations must provide non-null {@code name} and {@code stream}.
 *
 * @param name   the name of the resource for identification and resolution; never {@code null}
 * @param stream the {@link InputStream} providing the resource content; never {@code null}
 */
public record DefaultResourceStream(String name, InputStream stream) implements
    ResourceStream {

  public DefaultResourceStream {
    requireNonNull(name, "name must not be null");
    requireNonNull(stream, "stream must not be null");
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
