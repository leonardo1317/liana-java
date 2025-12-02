package io.github.liana.config.core.logging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsoleLoggerTest {

  @Test
  @DisplayName("should return non-null logger instances")
  void shouldReturnNonNullLoggerInstances() {
    Logger defaultLogger = ConsoleLogger.getLogger();
    Logger verboseLogger = ConsoleLogger.getLogger(true);

    assertNotNull(defaultLogger);
    assertNotNull(verboseLogger);
    assertNotSame(defaultLogger, verboseLogger);
  }

  @Test
  @DisplayName("should log DEBUG message when level is enabled")
  void shouldLogDebugMessageWhenLevelEnabled() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    logger.debug(() -> "debug message");

    String printed = output.toString();

    assertTrue(printed.contains("debug message"));
    assertTrue(printed.contains("DEBUG"));
  }

  @Test
  @DisplayName("should not log DEBUG message when level is disabled")
  void shouldNotLogDebugMessageWhenLevelDisabled() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(false);
    logger.debug(() -> "debug message");

    assertEquals("", output.toString());
  }
  
  @Test
  @DisplayName("should log INFO message when level is enabled")
  void shouldLogInfoMessageWhenLevelEnabled() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    logger.info(() -> "info message");

    String printed = output.toString();

    assertTrue(printed.contains("info message"));
    assertTrue(printed.contains("INFO"));
  }

  @Test
  @DisplayName("should not log INFO message when level is disabled")
  void shouldNotLogInfoMessageWhenLevelDisabled() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(false);
    logger.info(() -> "info message");

    assertEquals("", output.toString());
  }

  @Test
  @DisplayName("should log WARN message when level is enabled")
  void shouldLogWarnMessageWhenLevelEnabled() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    logger.warn(() -> "warn message");

    String printed = output.toString();

    assertTrue(printed.contains("warn message"));
    assertTrue(printed.contains("WARN"));
  }

  @Test
  @DisplayName("should log WARN message when level is default (non-verbose)")
  void shouldLogWarnMessageWhenDefaultLevel() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger();
    logger.warn(() -> "warn message");

    String printed = output.toString();

    assertTrue(printed.contains("warn message"));
    assertTrue(printed.contains("WARN"));
  }

  @Test
  @DisplayName("should log ERROR message when exception provided")
  void shouldLogErrorMessageWhenExceptionProvided() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    Exception exception = new RuntimeException("Test exception");

    logger.error(() -> "error occurred", exception);

    String printed = output.toString();

    assertTrue(printed.contains("error occurred"));
    assertTrue(printed.contains("Test exception"));
    assertTrue(printed.contains("ERROR"));
  }

  @Test
  @DisplayName("should log ERROR message without exception when level is enabled")
  void shouldLogErrorMessageWithoutExceptionWhenEnabled() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    logger.error(() -> "simple error", null);

    String printed = output.toString();

    assertTrue(printed.contains("simple error"));
    assertTrue(printed.contains("ERROR"));
  }

  @Test
  @DisplayName("should include timestamp, thread name, log level and logger name in output format")
  void shouldIncludeAllMetadataInLogOutput() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    logger.info(() -> "formatted message test");

    String printed = output.toString().trim();

    assertTrue(printed.matches(
        "\\d{2}:\\d{2}:\\d{2}\\.\\d{3} \\[.+] INFO io\\.github\\.liana\\.config .*"));
  }

  @Test
  @DisplayName("should handle null supplier gracefully without throwing exceptions")
  void shouldHandleNullSupplierGracefully() {
    Logger logger = ConsoleLogger.getLogger(true);
    assertDoesNotThrow(() -> logger.debug(null));
  }

  @Test
  @DisplayName("should replace null message with empty string")
  void shouldReplaceNullMessageWithEmptyString() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    Logger logger = ConsoleLogger.getLogger(true);
    Supplier<String> nullMessageSupplier = () -> null;

    assertDoesNotThrow(() -> logger.info(nullMessageSupplier));
    String printed = output.toString();

    assertTrue(printed.contains("INFO"));
  }
}
