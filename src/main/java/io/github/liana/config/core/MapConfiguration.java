package io.github.liana.config.core;

import java.util.Map;

/**
 * Immutable configuration backed by a nested {@link Map}.
 *
 * <p>This class is a concrete implementation of {@link AbstractConfiguration} that sources
 * configuration data from a {@link Map}. Access to configuration properties is delegated to the
 * parent class.
 *
 * <p>Intended for cases where configuration is already available in memory as a {@link Map}
 * and does not require custom parsing or loading logic.
 *
 * <p><b>Immutability:</b> The configuration is effectively immutable; modifications to the
 * original map after construction do not affect this instance.
 *
 * <p><b>Thread Safety:</b> Instances are thread-safe as long as the underlying
 * {@link ValueResolver} is thread-safe.
 */
public class MapConfiguration extends AbstractConfiguration {

  /**
   * Creates a new {@code MapConfiguration} with the given nested map as its backing store.
   *
   * @param nestedMap the map containing configuration properties; must not be {@code null}
   * @throws NullPointerException if {@code nestedMap} is {@code null}
   */
  public MapConfiguration(Map<String, Object> nestedMap) {
    super(new JacksonValueResolver(nestedMap));
  }
}
