package io.github.liana.config.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.core.exception.MissingConfigException;
import io.github.liana.config.core.type.TypeOf;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigurationTest {

  @Mock
  private Configuration config;

  @Test
  @DisplayName("should return default value when key is missing (TypeOf overload)")
  void shouldReturnDefaultValueWhenKeyIsMissingTypeOf() {
    TypeOf<String> type = new TypeOf<>() {
    };
    when(config.get("missing.key", type)).thenReturn(Optional.empty());
    when(config.get("missing.key", type, "default")).thenCallRealMethod();

    String result = config.get("missing.key", type, "default");
    assertEquals("default", result);
  }

  @Test
  @DisplayName("should return actual value when key is present (TypeOf overload)")
  void shouldReturnActualValueWhenKeyIsPresentTypeOf() {
    TypeOf<String> type = new TypeOf<>() {
    };
    when(config.get("existing.key", type)).thenReturn(Optional.of("value"));
    when(config.get("existing.key", type, "default")).thenCallRealMethod();

    String result = config.get("existing.key", type, "default");
    assertEquals("value", result);
  }

  @Test
  @DisplayName("should return default value when key is missing (Class overload)")
  void shouldReturnDefaultValueWhenKeyIsMissingClass() {
    when(config.get("missing.key", String.class)).thenReturn(Optional.empty());
    when(config.get("missing.key", String.class, "default")).thenCallRealMethod();

    assertEquals("default", config.get("missing.key", String.class, "default"));
  }

  @Test
  @DisplayName("should return actual value when key is present (Class overload)")
  void shouldReturnActualValueWhenKeyIsPresentClass() {
    when(config.get("app.name", String.class)).thenReturn(Optional.of("Liana"));
    when(config.get("app.name", String.class, "default")).thenCallRealMethod();

    assertEquals("Liana", config.get("app.name", String.class, "default"));
  }

  @Test
  @DisplayName("should throw MissingConfigException when key is missing")
  void shouldThrowMissingConfigExceptionWhenKeyIsMissing() {
    when(config.get("required.key", String.class)).thenReturn(Optional.empty());
    when(config.getOrThrow("required.key", String.class)).thenCallRealMethod();

    assertThrows(MissingConfigException.class,
        () -> config.getOrThrow("required.key", String.class));
  }

  @Test
  @DisplayName("should return value when key is present")
  void shouldReturnValueWhenKeyIsPresent() {
    when(config.get("required.key", Integer.class)).thenReturn(Optional.of(42));
    when(config.getOrThrow("required.key", Integer.class)).thenCallRealMethod();

    assertEquals(42, config.getOrThrow("required.key", Integer.class));
  }

  @Test
  @DisplayName("should throw MissingConfigException when TypeOf key missing")
  void shouldThrowMissingConfigExceptionWhenTypeOfKeyMissing() {
    TypeOf<String> type = new TypeOf<>() {
    };
    when(config.get("required.key", type)).thenReturn(Optional.empty());
    when(config.getOrThrow("required.key", type)).thenCallRealMethod();

    assertThrows(MissingConfigException.class,
        () -> config.getOrThrow("required.key", type));
  }

  @Test
  @DisplayName("should return value when TypeOf key present")
  void shouldReturnValueWhenTypeOfKeyPresent() {
    TypeOf<Double> type = new TypeOf<>() {
    };
    when(config.get("pi", type)).thenReturn(Optional.of(3.14));
    when(config.getOrThrow("pi", type)).thenCallRealMethod();

    assertEquals(3.14, config.getOrThrow("pi", type));
  }

  @Test
  @DisplayName("should delegate to getOrThrow for getString")
  void shouldDelegateToGetOrThrowForGetString() {
    when(config.getOrThrow(anyString(), eq(String.class))).thenReturn("service");
    when(config.getString("name")).thenCallRealMethod();

    assertEquals("service", config.getString("name"));
    verify(config).getOrThrow(anyString(), eq(String.class));
  }

  @Test
  @DisplayName("should delegate to getOrThrow for getInt")
  void shouldDelegateToGetOrThrowForGetInt() {
    when(config.getOrThrow("retries", Integer.class)).thenReturn(3);
    when(config.getInt("retries")).thenCallRealMethod();

    assertEquals(3, config.getInt("retries"));
  }

  @Test
  @DisplayName("should delegate to getOrThrow for getBoolean")
  void shouldDelegateToGetOrThrowForGetBoolean() {
    when(config.getOrThrow("enabled", Boolean.class)).thenReturn(true);
    when(config.getBoolean("enabled")).thenCallRealMethod();

    assertTrue(config.getBoolean("enabled"));
  }

  @Test
  @DisplayName("should delegate to getOrThrow for getDouble")
  void shouldDelegateToGetOrThrowForGetDouble() {
    when(config.getOrThrow("threshold", Double.class)).thenReturn(2.5);
    when(config.getDouble("threshold")).thenCallRealMethod();

    assertEquals(2.5, config.getDouble("threshold"));
  }

  @Test
  @DisplayName("should delegate to getOrThrow for getDuration")
  void shouldDelegateToGetOrThrowForGetDuration() {
    Duration expected = Duration.ofSeconds(10);
    when(config.getOrThrow("timeout", Duration.class)).thenReturn(expected);
    when(config.getDuration("timeout")).thenCallRealMethod();

    assertEquals(expected, config.getDuration("timeout"));
  }

  @Test
  @DisplayName("should use default string value when key is missing")
  void shouldUseDefaultStringValueWhenKeyIsMissing() {
    String expected = "default-app";
    Configuration config = mock(Configuration.class, Mockito.CALLS_REAL_METHODS);
    lenient().when(config.get(anyString(), eq(String.class))).thenReturn(Optional.empty());

    String result = config.getString("app.name", expected);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("should use default int value when key is missing")
  void shouldUseDefaultIntValueWhenKeyIsMissing() {
    Integer expected = 3;
    Configuration config = mock(Configuration.class, Mockito.CALLS_REAL_METHODS);
    lenient().when(config.get(anyString(), eq(Integer.class))).thenReturn(Optional.empty());

    Integer result = config.getInt("retries", expected);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("should use default boolean value when key is missing")
  void shouldUseDefaultBooleanValueWhenKeyIsMissing() {
    boolean expected = true;
    Configuration config = mock(Configuration.class, Mockito.CALLS_REAL_METHODS);
    lenient().when(config.get(anyString(), eq(Boolean.class))).thenReturn(Optional.empty());

    Boolean result = config.getBoolean("enabled", expected);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("should use default double value when key is missing")
  void shouldUseDefaultDoubleValueWhenKeyIsMissing() {
    Double expected = 2.5;
    Configuration config = mock(Configuration.class, Mockito.CALLS_REAL_METHODS);
    lenient().when(config.get(anyString(), eq(Double.class))).thenReturn(Optional.empty());

    Double result = config.getDouble("threshold", expected);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("should use default duration value when key is missing")
  void shouldUseDefaultDurationValueWhenKeyIsMissing() {
    Duration expected = Duration.ofSeconds(10);
    Configuration config = mock(Configuration.class, Mockito.CALLS_REAL_METHODS);
    lenient().when(config.get(anyString(), eq(Duration.class))).thenReturn(Optional.empty());

    Duration result = config.getDuration("timeout", expected);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("should create MissingConfigException with expected message")
  void shouldCreateMissingConfigExceptionWithExpectedMessage() {
    when(config.get(anyString(), eq(String.class))).thenReturn(Optional.empty());
    when(config.getOrThrow(anyString(), eq(String.class))).thenCallRealMethod();

    MissingConfigException ex =
        assertThrows(MissingConfigException.class, () -> config.getOrThrow("error", String.class));

    assertTrue(ex.getMessage().contains("Missing required config: error"));
  }

  @Test
  @DisplayName("should create Configuration from a valid map and retrieve values")
  void shouldCreateConfigurationFromMap() {
    Map<String, Object> nestedMap = Map.of(
        "app", Map.of("name", "Liana", "retries", 3)
    );

    Configuration config = Configuration.from(nestedMap);

    assertEquals("Liana", config.getString("app.name"));
    assertEquals(3, config.getInt("app.retries"));
  }

  @Test
  @DisplayName("should throw NullPointerException when null map is passed")
  void shouldThrowExceptionWhenMapIsNull() {
    assertThrows(NullPointerException.class, () -> Configuration.from(null));
  }
}