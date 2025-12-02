package io.github.liana.config.core;

import io.github.liana.config.api.Placeholder;
import io.github.liana.config.api.PlaceholderBuilder;

/**
 * Default implementation of {@link PlaceholderBuilder}.
 *
 * <p>This builder provides a mutable configuration mechanism for assembling
 * {@link Placeholder} instances with a customizable syntax. It uses conventional defaults such as
 * the prefix {@code "${"} and suffix {@code "}"}.
 *
 * <p>This class is part of the public API. It is mutable and therefore not
 * thread-safe. A new builder instance should be used for each independent construction sequence.
 *
 * <p><strong>Limitations:</strong> No validation is performed on the configured
 * values, which may lead to invalid {@link Placeholder} instances depending on the expectations of
 * the underlying implementation.
 */
public final class DefaultPlaceholderBuilder implements PlaceholderBuilder {

  private String prefix = "${";
  private String suffix = "}";
  private String delimiter = ":";
  private char escapeChar = '\\';

  @Override
  public PlaceholderBuilder prefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  @Override
  public PlaceholderBuilder suffix(String suffix) {
    this.suffix = suffix;
    return this;
  }

  @Override
  public PlaceholderBuilder delimiter(String delimiter) {
    this.delimiter = delimiter;
    return this;
  }

  @Override
  public PlaceholderBuilder escapeChar(char escapeChar) {
    this.escapeChar = escapeChar;
    return this;
  }

  /**
   * Creates a new {@link Placeholder} instance using the current configuration.
   *
   * <p>The produced instance is independent of any subsequent modifications
   * performed on this builder.
   *
   * @return a newly created {@code Placeholder}
   */
  @Override
  public Placeholder build() {
    return new DefaultPlaceholder(prefix, suffix, delimiter, escapeChar);
  }
}
