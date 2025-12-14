package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A thread-safe registry that discovers and caches singleton instances of type {@code R} from
 * service implementations of type {@code T} using a {@link ServiceLoader}.
 *
 * <p>This class supports dynamic service resolution and caching by:
 * <ul>
 *   <li>Loading implementations of {@code T} via {@link ServiceLoader}.</li>
 *   <li>Filtering services with a {@link BiPredicate} to match type aliases.</li>
 *   <li>Converting matching services into {@code R} instances using a {@link Function}.</li>
 *   <li>Caching created instances to ensure singleton-like behavior for each type alias.</li>
 * </ul>
 *
 * <p>Keys (type aliases) are converted to lowercase to allow case-insensitive lookups.
 * All created instances are cached to avoid redundant object creation.
 *
 * <p>Thread-safety is guaranteed through the use of a {@link LoadingCache}.
 *
 * @param <T> the type of service loaded by {@link ServiceLoader}
 * @param <R> the type of object produced from the service
 */
class ServiceRegistry<T, R> {

  private final ServiceLoader<T> loader;
  private final LoadingCache<String, R> cache = new LoadingCache<>();
  private final BiPredicate<T, String> filter;
  private final Function<T, R> function;

  /**
   * Constructs a new {@code ServiceRegistry} with the given loader, filter, and factory function.
   *
   * @param loader   the {@link ServiceLoader} used to discover service implementations; must not be
   *                 {@code null}
   * @param filter   a {@link BiPredicate} returning {@code true} if a service matches the requested
   *                 type alias; must not be {@code null}
   * @param function a {@link Function} converting a matching service of type {@code T} into
   *                 {@code R}; must not be {@code null}
   * @throws NullPointerException if any parameter is {@code null}
   */
  public ServiceRegistry(ServiceLoader<T> loader,
      BiPredicate<T, String> filter,
      Function<T, R> function) {
    this.loader = requireNonNull(loader, "loader must not be null");
    this.filter = requireNonNull(filter, "filter must not be null");
    this.function = requireNonNull(function, "function must not be null");
  }

  /**
   * Returns a cached singleton instance of {@code R} for the given type alias.
   *
   * <p>If no cached instance exists, this method discovers a matching service, converts it using
   * the factory function, caches it, and returns it.
   *
   * @param type the type alias to look up; must not be {@code null}
   * @return an {@link Optional} containing the singleton instance of {@code R}, or empty if no
   * matching service is found
   * @throws NullPointerException if {@code type} is {@code null}
   */
  public Optional<R> get(String type) {
    requireNonNull(type, "type must not be null");
    var normalizedType = type.toLowerCase(Locale.ROOT);
    return Optional.ofNullable(cache.getOrCompute(normalizedType, () -> create(normalizedType)));
  }

  private R create(String type) {
    for (T service : loader) {
      if (filter.test(service, type)) {
        return function.apply(service);
      }
    }
    return null;
  }
}
