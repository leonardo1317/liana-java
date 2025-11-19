package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigLoaderException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigLoaderResolverTest {

  @Mock
  private StrategyRegistry<String, ConfigLoader> strategies;

  @Mock
  private ConfigLoader configLoader;

  private ConfigLoaderResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new ConfigLoaderResolver(strategies);
  }

  @Test
  @DisplayName("should create resolver with non-null strategies")
  void shouldCreateWithNonNullStrategies() {
    assertNotNull(resolver);
  }

  @Test
  @DisplayName("should throw NullPointerException when strategies is null")
  void shouldThrowWhenStrategiesIsNull() {
    assertThrows(NullPointerException.class, () -> new ConfigLoaderResolver(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when resourceName is null")
  void shouldThrowWhenResourceNameIsNull() {
    assertThrows(IllegalArgumentException.class, () -> resolver.resolve(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when resourceName is blank")
  void shouldThrowWhenResourceNameIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> resolver.resolve("   "));
  }

  @Test
  @DisplayName("should throw ConfigLoaderException when extension is unsupported")
  void shouldThrowWhenExtensionUnsupported() {
    when(strategies.get("unknown")).thenReturn(Optional.empty());

    ConfigLoaderException ex = assertThrows(
        ConfigLoaderException.class,
        () -> resolver.resolve("file.unknown")
    );

    assertTrue(ex.getMessage().contains("no config loader registered"));
  }

  @Test
  @DisplayName("should return loader when extension is supported")
  void shouldReturnLoaderWhenSupported() {
    when(strategies.get("json")).thenReturn(Optional.of(configLoader));

    ConfigLoader result = resolver.resolve("config.json");

    assertSame(configLoader, result);
  }

  @Test
  @DisplayName("should use lowercase extension for lookup")
  void shouldUseLowercaseExtension() {
    when(strategies.get("yaml")).thenReturn(Optional.of(configLoader));

    ConfigLoader result = resolver.resolve("CONFIG.YAML");

    assertSame(configLoader, result);
    verify(strategies).get("yaml");
  }

  @Test
  @DisplayName("should throw ConfigLoaderException when no extension exists")
  void shouldThrowWhenNoExtension() {
    ConfigLoaderException ex = assertThrows(
        ConfigLoaderException.class,
        () -> resolver.resolve("config")
    );

    assertTrue(ex.getMessage().contains("no config loader registered"));
  }
}
