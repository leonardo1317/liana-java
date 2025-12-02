package io.github.liana.config.core;

/**
 * Represents an action that can be executed and may throw checked exceptions.
 *
 * <p>This is a {@link FunctionalInterface} similar to {@link java.lang.Runnable},
 * but allows the {@link #run()} method to throw any {@link Exception}.
 *
 * <p>Typical usage is in utility methods that wrap execution and handle exceptions consistently,
 * for example {@link AbstractJacksonComponent#executeAction(ThrowingRunnable, String)}.
 */
@FunctionalInterface
public interface ThrowingRunnable {

  /**
   * Executes the action, possibly throwing a checked exception.
   *
   * @throws Exception if the execution fails
   */
  void run() throws Exception;
}
