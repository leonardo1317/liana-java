package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.liana.config.spi.ResourceProvider;
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
    List<String> directories = List.of("", "config");
    List<String> expected = List.of("classpath", "filesystem");
    ResourceProvider customProvider = mock(ResourceProvider.class);

    when(customProvider.getKeys()).thenReturn(Set.of("filesystem"));

    ProvidersRegistry providersRegistry = new ProvidersRegistry(List.of(customProvider));

    StrategyRegistry<String, ResourceProvider> registry = providersRegistry.create(directories);

    assertNotNull(registry);
    assertEquals(2, registry.getAllKeys().size());
    assertIterableEquals(expected, registry.getAllKeys());
  }

  @Test
  @DisplayName("should return registry with only default provider when no custom providers are given")
  void shouldReturnRegistryWithOnlyDefaultProviderWhenNoCustomProvidersGiven() {
    List<String> directories = List.of("", "config");
    ProvidersRegistry providersRegistry = new ProvidersRegistry(Collections.emptyList());

    StrategyRegistry<String, ResourceProvider> registry = providersRegistry.create(directories);

    assertNotNull(registry);
    assertEquals(1, registry.getAllKeys().size());
    assertIterableEquals(List.of("classpath"), registry.getAllKeys());
  }
}
