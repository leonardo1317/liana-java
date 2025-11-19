package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigProviderException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigProviderResolverTest {

  @Mock
  private StrategyRegistry<String, ConfigProvider> strategies;

  @Mock
  private ConfigProvider configProvider;

  private ConfigProviderResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new ConfigProviderResolver(strategies);
  }

  @Test
  @DisplayName("constructor should throw NullPointerException when strategies is null")
  void shouldThrowWhenStrategiesIsNull() {
    assertThrows(NullPointerException.class, () -> new ConfigProviderResolver(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when provider identifier is null or blank")
  void shouldThrowWhenProviderIdentifierInvalid() {
    assertThrows(IllegalArgumentException.class, () -> resolver.resolve(null));
    assertThrows(IllegalArgumentException.class, () -> resolver.resolve(""));
    assertThrows(IllegalArgumentException.class, () -> resolver.resolve("  "));
  }

  @Test
  @DisplayName("should throw ConfigProviderException when provider not found")
  void shouldThrowWhenProviderNotFound() {
    when(strategies.get("missing")).thenReturn(Optional.empty());

    ConfigProviderException ex =
        assertThrows(ConfigProviderException.class, () -> resolver.resolve("missing"));

    assertEquals("no ConfigProvider is registered for identifier: missing", ex.getMessage());
  }

  @Test
  @DisplayName("should return provider when found in registry")
  void shouldReturnProviderWhenFound() {
    when(strategies.get("valid")).thenReturn(Optional.of(configProvider));

    ConfigProvider result = resolver.resolve("valid");

    assertSame(configProvider, result);
  }
}
