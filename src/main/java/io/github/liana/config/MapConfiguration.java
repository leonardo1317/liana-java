package io.github.liana.config;

import java.util.Map;
import java.util.Optional;

/**
 * Immutable configuration backed by a nested {@link Map}.
 *
 * <p>This class is a concrete implementation of {@link AbstractConfiguration} that sources its
 * configuration data from a {@link Map}. Keys and values are stored as provided, and access to
 * configuration properties is delegated to the parent class.
 *
 * <p>Intended for cases where configuration is already available in memory as a {@link Map} and
 * does not require custom parsing or loading logic.
 *
 * <p>Example usage:
 * <pre>{@code
 * Map<String, Object> configData = new HashMap<>();
 * configData.put("timeout", 30);
 * configData.put("feature.enabled", true);
 *
 * MapConfiguration config = new MapConfiguration(configData);
 *
 * Optional<Integer> timeout = config.get("timeout", Integer.class);
 * Optional<Boolean> featureFlag = config.get("feature.enabled", Boolean.class);
 * }</pre>
 */
public class MapConfiguration extends AbstractConfiguration {

  /**
   * Creates a new {@code MapConfiguration} with the given nested map as its backing store.
   *
   * @param nestedMap the map containing configuration properties; must not be {@code null}
   */
  protected MapConfiguration(Map<String, Object> nestedMap) {
    super(new JacksonValueResolver(nestedMap));
  }
}
