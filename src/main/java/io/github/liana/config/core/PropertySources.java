package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for creating {@link PropertySource} instances from various sources.
 *
 * <p>This final class provides factory methods for creating property sources backed by
 * environment variables, maps, or other {@link PropertySource} instances.
 *
 * <p>Instances of this class cannot be created. All methods are static and thread-safe.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Create property sources from environment variables.</li>
 *   <li>Create property sources from a {@link Map}, converting all values to strings.</li>
 *   <li>Provide identity wrapping for existing {@link PropertySource} instances.</li>
 * </ul>
 *
 * <p>Limitations:
 * <ul>
 *   <li>Values in map-based sources are converted to strings using {@link Object#toString()}.
 *       Null values are preserved as {@code null}.</li>
 *   <li>The class does not merge multiple sources; each source is independent.</li>
 * </ul>
 */
public final class PropertySources {

  private PropertySources() {
  }

  /**
   * Creates a {@link PropertySource} backed by the system environment variables.
   *
   * <p>The returned source resolves keys using {@link System#getenv(String)}.
   *
   * @return a property source that retrieves values from environment variables
   */
  public static PropertySource fromEnv() {
    return from(System::getenv);
  }

  /**
   * Creates a {@link PropertySource} backed by a custom resolver function.
   *
   * <p>This method is primarily intended for testing. Production code can use {@link #fromEnv()}.
   *
   * @param resolver function that maps a key to a value; must not be {@code null}
   * @return a property source using the provided resolver
   * @throws NullPointerException if {@code resolver} is {@code null}
   */
  public static PropertySource from(Function<String, String> resolver) {
    requireNonNull(resolver, "resolver must not be null");
    return resolver::apply;
  }

  /**
   * Creates a {@link PropertySource} backed by the given map.
   *
   * <p>The provided map is defensively copied and wrapped in an unmodifiable view.
   * All values are converted to strings using {@link Object#toString()}; {@code null} values remain
   * {@code null}.
   *
   * @param map the source map; if {@code null}, an empty map is used
   * @return a property source that retrieves values from the given map
   */
  public static PropertySource fromMap(Map<String, ?> map) {
    Map<String, ?> source = Collections.unmodifiableMap(
        new HashMap<>(requireNonNullElse(map, Collections.emptyMap())));
    return key -> source.get(key) != null ? source.get(key).toString() : null;
  }
}
