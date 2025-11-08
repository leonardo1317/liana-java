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
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigResourceLoaderTest {

  @Mock
  private StrategyRegistry<String, ConfigLoader> strategies;

  @Mock
  private ConfigLoader configLoader;

  @Mock
  private ConfigResource resource;

  @Mock
  private Configuration configuration;

  private ConfigResourceLoader loader;

  @BeforeEach
  void setUp() {
    loader = ConfigResourceLoader.of(strategies);
  }

  @Test
  @DisplayName("should create ConfigResourceLoader with non-null strategies")
  void shouldCreateWithNonNullStrategies() {
    assertNotNull(loader);
  }

  @Test
  @DisplayName("should throw NullPointerException when strategies is null")
  void shouldThrowWhenStrategiesIsNull() {
    assertThrows(NullPointerException.class, () -> ConfigResourceLoader.of(null));
  }

  @Test
  @DisplayName("should throw NullPointerException when resource is null")
  void shouldThrowWhenResourceIsNull() {
    assertThrows(NullPointerException.class, () -> loader.loadFromResource(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when resourceName is null")
  void shouldThrowWhenResourceNameIsNull() {
    when(resource.resourceName()).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> loader.loadFromResource(resource));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when resourceName is blank")
  void shouldThrowWhenResourceNameIsBlank() {
    when(resource.resourceName()).thenReturn("   ");
    assertThrows(IllegalArgumentException.class, () -> loader.loadFromResource(resource));
  }

  @Test
  @DisplayName("should throw ConfigLoaderException when extension is unsupported")
  void shouldThrowWhenExtensionUnsupported() {
    when(resource.resourceName()).thenReturn("file.unknown");
    when(strategies.get("unknown")).thenReturn(Optional.empty());

    ConfigLoaderException ex = assertThrows(ConfigLoaderException.class,
        () -> loader.loadFromResource(resource));

    assertTrue(ex.getMessage().contains("unsupported config file"));
  }

  @Test
  @DisplayName("should delegate to ConfigLoader when extension is supported")
  void shouldDelegateToConfigLoader() {
    when(resource.resourceName()).thenReturn("config.json");
    when(strategies.get("json")).thenReturn(Optional.of(configLoader));
    when(configLoader.load(resource)).thenReturn(configuration);

    Configuration result = loader.loadFromResource(resource);

    assertSame(configuration, result);
    verify(configLoader).load(resource);
  }

  @Test
  @DisplayName("should rethrow ConfigLoaderException from ConfigLoader")
  void shouldRethrowConfigLoaderException() {
    when(resource.resourceName()).thenReturn("config.yaml");
    when(strategies.get("yaml")).thenReturn(Optional.of(configLoader));
    when(configLoader.load(resource)).thenThrow(new ConfigLoaderException("bad config"));

    assertThrows(ConfigLoaderException.class, () -> loader.loadFromResource(resource));
  }

  @Test
  @DisplayName("should wrap unexpected exception into ConfigLoaderException")
  void shouldWrapUnexpectedException() {
    when(resource.resourceName()).thenReturn("config.xml");
    when(strategies.get("xml")).thenReturn(Optional.of(configLoader));
    when(configLoader.load(resource)).thenThrow(new RuntimeException("boom"));

    ConfigLoaderException ex = assertThrows(ConfigLoaderException.class,
        () -> loader.loadFromResource(resource));

    assertTrue(ex.getMessage().contains("unexpected error"));
    assertInstanceOf(RuntimeException.class, ex.getCause());
  }
}
