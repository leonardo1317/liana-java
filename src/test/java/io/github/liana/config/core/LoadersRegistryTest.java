package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.spi.ResourceLoader;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoadersRegistryTest {

  @Mock
  private JacksonMappers jacksonMappers;

  @Test
  @DisplayName("should throw NullPointerException when custom loaders list is null")
  void shouldThrowExceptionWhenCustomLoadersIsNull() {
    assertThrows(NullPointerException.class,
        () -> new LoadersRegistry(null, jacksonMappers));
  }

  @Test
  @DisplayName("should throw NullPointerException when jacksonMappers is null")
  void shouldThrowExceptionWhenJacksonMappersIsNull() {
    assertThrows(NullPointerException.class,
        () -> new LoadersRegistry(List.of(), null));
  }

  @Test
  @DisplayName("should create registry containing default loaders and custom ones")
  void shouldCreateRegistryContainingDefaultAndCustomLoaders() {
    List<String> expected = List.of("properties", "yaml", "yml", "json", "xml", "toml");
    ResourceLoader customLoader = mock(ResourceLoader.class);
    ObjectMapper objectMapper = mock(ObjectMapper.class);

    when(customLoader.getKeys()).thenReturn(Set.of("toml"));
    when(jacksonMappers.getProperties()).thenReturn(objectMapper);
    when(jacksonMappers.getYaml()).thenReturn(objectMapper);
    when(jacksonMappers.getJson()).thenReturn(objectMapper);
    when(jacksonMappers.getXml()).thenReturn(objectMapper);

    LoadersRegistry loadersRegistry = new LoadersRegistry(
        List.of(customLoader),
        jacksonMappers
    );

    StrategyRegistry<String, ResourceLoader> registry = loadersRegistry.create();

    assertNotNull(registry);
    assertEquals(6, registry.getAllKeys().size());
    assertIterableEquals(expected, registry.getAllKeys());
  }

  @Test
  @DisplayName("should create registry containing only default loaders when no custom loaders provided")
  void shouldCreateRegistryWithOnlyDefaultLoadersWhenCustomEmpty() {
    List<String> expected = List.of("properties", "yaml", "yml", "json", "xml");
    ObjectMapper objectMapper = mock(ObjectMapper.class);

    when(jacksonMappers.getProperties()).thenReturn(objectMapper);
    when(jacksonMappers.getYaml()).thenReturn(objectMapper);
    when(jacksonMappers.getJson()).thenReturn(objectMapper);
    when(jacksonMappers.getXml()).thenReturn(objectMapper);

    LoadersRegistry loadersRegistry = new LoadersRegistry(
        Collections.emptyList(),
        jacksonMappers
    );

    StrategyRegistry<String, ResourceLoader> registry = loadersRegistry.create();

    assertNotNull(registry);
    assertEquals(5, registry.getAllKeys().size());
    assertIterableEquals(expected, registry.getAllKeys());
  }
}
