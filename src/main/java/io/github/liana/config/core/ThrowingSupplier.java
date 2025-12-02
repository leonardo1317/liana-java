package io.github.liana.config.core;

/**
 * Represents a supplier of results that can throw checked exceptions.
 *
 * <p>This is a {@link FunctionalInterface} similar to {@link java.util.function.Supplier},
 * but allows the {@link #get()} method to throw any {@link Exception}.
 *
 * <p>Typical usage is in utility methods that wrap execution and handle exceptions consistently,
 * for example {@link AbstractJacksonComponent#executeWithResult(ThrowingSupplier, String)}.
 *
 * @param <T> the type of results supplied by this supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

  /**
   * Gets a result, possibly throwing a checked exception.
   *
   * @return the result
   * @throws Exception if unable to produce a result
   */
  T get() throws Exception;
}
