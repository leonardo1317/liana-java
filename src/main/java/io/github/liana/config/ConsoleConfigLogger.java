package io.github.liana.config;

import static java.util.Objects.requireNonNullElse;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

final class ConsoleConfigLogger implements ConfigLogger {

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

  private ConsoleConfigLogger() {
    this(false);
  }

  private ConsoleConfigLogger(boolean verboseLogging) {
    this.configuredLevel = verboseLogging ? LogLevel.DEBUG : LogLevel.WARN;
  }

  public static ConfigLogger getLogger() {
    return new ConsoleConfigLogger();
  }

  public static ConfigLogger getLogger(boolean verboseLogging) {
    return new ConsoleConfigLogger(verboseLogging);
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
