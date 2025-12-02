package io.github.liana.config.core.logging;

import static java.util.Objects.requireNonNullElse;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/**
 * A simple {@link Logger} implementation that logs messages to the console.
 *
 * <p>Messages are printed to {@link System#out} with a timestamp, thread name,
 * log level, and logger name. Exceptions are printed with their stack trace.
 *
 * <p>The logging level can be configured to enable verbose output (DEBUG and higher)
 * or default to WARN and higher. Lower-severity messages are ignored when the configured level is
 * higher.
 *
 * <p>All messages are evaluated lazily via {@link Supplier}, avoiding unnecessary
 * computation if the message is not logged.
 *
 * <p>Use the static factory methods {@link #getLogger()} or {@link #getLogger(boolean)}
 * to obtain an instance.
 */
public class ConsoleLogger implements Logger {

  private static final String LOGGER_NAME = "io.github.liana.config";
  private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(
      "HH:mm:ss.SSS");
  private final LogLevel configuredLevel;

  private enum LogLevel {
    DEBUG, INFO, WARN, ERROR;

    private boolean isEnabled(LogLevel configuredLevel) {
      return this.ordinal() >= configuredLevel.ordinal();
    }
  }

  private ConsoleLogger() {
    this(false);
  }

  private ConsoleLogger(boolean verboseLogging) {
    this.configuredLevel = verboseLogging ? LogLevel.DEBUG : LogLevel.WARN;
  }

  /**
   * Returns a console logger instance with default logging level (WARN and above).
   *
   * @return a {@link Logger} instance
   */
  public static Logger getLogger() {
    return new ConsoleLogger();
  }

  /**
   * Returns a console logger instance with optional verbose logging.
   *
   * @param verboseLogging if {@code true}, enables DEBUG and higher-level messages; otherwise WARN
   *                       and above
   * @return a {@link Logger} instance
   */
  public static Logger getLogger(boolean verboseLogging) {
    return new ConsoleLogger(verboseLogging);
  }

  @Override
  public void info(Supplier<String> message) {
    log(LogLevel.INFO, message);
  }

  @Override
  public void debug(Supplier<String> message) {
    log(LogLevel.DEBUG, message);
  }

  @Override
  public void warn(Supplier<String> message) {
    log(LogLevel.WARN, message);
  }

  @Override
  public void error(Supplier<String> message, Exception e) {
    log(LogLevel.ERROR, message, e);
  }

  private void log(LogLevel level, Supplier<String> supplier) {
    log(level, supplier, null);
  }

  private void log(LogLevel level, Supplier<String> supplier, Exception e) {
    if (supplier == null || !level.isEnabled(configuredLevel)) {
      return;
    }

    String message = requireNonNullElse(supplier.get(), "");
    String threadName = Thread.currentThread().getName();
    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);

    String formattedMessage = String.format("%s [%s] %s %s -- %s",
        timestamp, threadName, level.name(), LOGGER_NAME, message);

    PrintStream outputStream = System.out;
    outputStream.println(formattedMessage);

    if (e != null) {
      e.printStackTrace(outputStream);
    }
  }
}
