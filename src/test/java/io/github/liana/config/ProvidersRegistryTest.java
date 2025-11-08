package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProvidersRegistryTest {

  @Test
  @DisplayName("should throw NullPointerException when customProviders is null")
  void shouldThrowExceptionWhenCustomProvidersIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ProvidersRegistry(null));
  }

  @Test
  @DisplayName("should create registry containing default and custom providers")
  void shouldCreateRegistryContainingDefaultAndCustomProviders() {
    List<String> expected = List.of("classpath", "filesystem");
    ConfigProvider customProvider = mock(ConfigProvider.class);
    ClasspathResource resource = mock(ClasspathResource.class);

    when(customProvider.getKeys()).thenReturn(Set.of("filesystem"));

    ProvidersRegistry providersRegistry = new ProvidersRegistry(List.of(customProvider));

    StrategyRegistry<String, ConfigProvider> registry = providersRegistry.create(resource);

    assertNotNull(registry);
    assertEquals(2, registry.getAllKeys().size());
    assertIterableEquals(expected, registry.getAllKeys());
  }

  @Test
  @DisplayName("should return registry with only default provider when no custom providers are given")
  void shouldReturnRegistryWithOnlyDefaultProviderWhenNoCustomProvidersGiven() {
    ClasspathResource resource = mock(ClasspathResource.class);

    ProvidersRegistry providersRegistry = new ProvidersRegistry(Collections.emptyList());

    StrategyRegistry<String, ConfigProvider> registry = providersRegistry.create(resource);

    assertNotNull(registry);
    assertEquals(1, registry.getAllKeys().size());
    assertIterableEquals(List.of("classpath"), registry.getAllKeys());
  }
}
