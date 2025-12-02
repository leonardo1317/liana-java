package io.github.liana.config.core.logging;

import java.util.function.Supplier;

/**
 * Provides a generic interface for logging configuration-related messages.
 *
 * <p>Supports multiple log levels: info, debug, warn, and error. Messages are supplied
 * lazily via {@link Supplier} to avoid unnecessary computation when the logging level is disabled.
 *
 * <p>Implementations may delegate to any underlying logging framework or custom logging strategy.
 */
public interface Logger {

  /**
   * Logs an informational message.
   *
   * <p>The message is evaluated only if the info level is enabled.
   *
   * @param message a {@link Supplier} providing the log message
   */
  void info(Supplier<String> message);

  /**
   * Logs a debug-level message.
   *
   * <p>The message is evaluated only if the debug level is enabled.
   *
   * @param message a {@link Supplier} providing the log message
   */
  void debug(Supplier<String> message);

  /**
   * Logs a warning-level message.
   *
   * <p>The message is evaluated only if the warn level is enabled.
   *
   * @param message a {@link Supplier} providing the log message
   */
  void warn(Supplier<String> message);

  /**
   * Logs an error-level message along with an exception.
   *
   * <p>The message is evaluated only if the error level is enabled.
   * The exception can provide stack trace or additional context.
   *
   * @param message a {@link Supplier} providing the log message
   * @param e       the {@link Exception} associated with the error
   */
  void error(Supplier<String> message, Exception e);
}
