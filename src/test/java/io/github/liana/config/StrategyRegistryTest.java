package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StrategyRegistryTest {

  @Test
  @DisplayName("should register strategies using default normalizer and retrieve them")
  void shouldRegisterStrategiesWithDefaultNormalizer() {
    ConfigLoader strategyYaml = mock(ConfigLoader.class);
    ConfigLoader strategyJson = mock(ConfigLoader.class);
    when(strategyYaml.getKeys()).thenReturn(Collections.singleton("yaml"));
    when(strategyJson.getKeys()).thenReturn(Collections.singleton("json"));

    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>(strategyYaml,
        strategyJson);

    assertTrue(registry.get("yaml").isPresent());
    assertTrue(registry.get("json").isPresent());
  }

  @Test
  @DisplayName("should register strategies using a custom normalizer (upper case) and retrieve by normalized key")
  void shouldRegisterStrategiesWithCustomNormalizer() {
    KeyNormalizer<String> upper = String::toUpperCase;
    ConfigLoader strategyJson = mock(ConfigLoader.class);
    when(strategyJson.getKeys()).thenReturn(Collections.singleton("json"));

    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>(upper, strategyJson);

    Optional<ConfigLoader> result = registry.get("JSON");
    assertTrue(result.isPresent());
    assertEquals("json", result.get().getKeys().iterator().next());
  }

  @Test
  @DisplayName("should replace previously registered strategy when same normalized key is registered later")
  void shouldReplaceStrategyWhenSameNormalizedKey() {
    KeyNormalizer<String> lower = String::toLowerCase;
    ConfigLoader first = mock(ConfigLoader.class);
    ConfigLoader second = mock(ConfigLoader.class);
    when(first.getKeys()).thenReturn(Collections.singleton("JSON"));
    when(second.getKeys()).thenReturn(Collections.singleton("json"));

    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>(lower, first, second);

    Optional<ConfigLoader> result = registry.get("json");

    assertTrue(result.isPresent());
    assertEquals(second, result.get());
  }

  @Test
  @DisplayName("should return empty Optional when key is not present")
  void shouldReturnEmptyWhenKeyNotPresent() {
    ConfigLoader strategyJson = mock(ConfigLoader.class);
    when(strategyJson.getKeys()).thenReturn(Collections.singleton("json"));

    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>(strategyJson);

    assertTrue(registry.get("yml").isEmpty());
  }


  @Test
  @DisplayName("should return all registered keys when strategies are present")
  void shouldReturnAllRegisteredKeys() {
    ConfigLoader yamlLoader = mock(ConfigLoader.class);
    ConfigLoader jsonLoader = mock(ConfigLoader.class);
    when(yamlLoader.getKeys()).thenReturn(Set.of("yaml"));
    when(jsonLoader.getKeys()).thenReturn(Set.of("json"));

    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>(yamlLoader, jsonLoader);
    Set<String> actualKeys = registry.getAllKeys();

    assertEquals(Set.of("yaml", "json"), actualKeys);
  }

  @Test
  @DisplayName("should return empty set when no strategies are registered")
  void shouldReturnEmptySetWhenNoStrategiesRegistered() {
    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>();

    Set<String> actualKeys = registry.getAllKeys();

    assertTrue(actualKeys.isEmpty());
  }

  @Test
  @DisplayName("should throw NullPointerException when varargs array passed to constructor is null")
  void shouldThrowWhenVarargsArrayIsNull() {
    assertThrows(NullPointerException.class, () -> new StrategyRegistry<>((ConfigLoader[]) null));
  }

  @Test
  @DisplayName("should throw NullPointerException when collection passed to constructor is null")
  void shouldThrowWhenCollectionIsNull() {
    assertThrows(NullPointerException.class,
        () -> new StrategyRegistry<>(key -> key, (Collection<ConfigLoader>) null));
  }

  @Test
  @DisplayName("should throw NullPointerException when keyNormalizer is null (varargs constructor)")
  void shouldThrowWhenKeyNormalizerIsNullInVarargsConstructor() {
    ConfigLoader strategyJson = mock(ConfigLoader.class);
    when(strategyJson.getKeys()).thenReturn(Collections.singleton("json"));

    assertThrows(NullPointerException.class,
        () -> new StrategyRegistry<>((KeyNormalizer<String>) null, strategyJson));
  }

  @Test
  @DisplayName("should throw NullPointerException when keyNormalizer is null (collection constructor)")
  void shouldThrowWhenKeyNormalizerIsNullInCollectionConstructor() {
    ConfigLoader strategyJson = mock(ConfigLoader.class);
    when(strategyJson.getKeys()).thenReturn(Collections.singleton("json"));

    assertThrows(NullPointerException.class,
        () -> new StrategyRegistry<>(null, List.of(strategyJson)));
  }

  @Test
  @DisplayName("should throw NullPointerException when a strategy contains a null key")
  void shouldThrowWhenStrategyHasNullKey() {
    ConfigLoader strategyYaml = mock(ConfigLoader.class);
    when(strategyYaml.getKeys()).thenReturn(
        Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList("yaml", null))));

    assertThrows(NullPointerException.class,
        () -> new StrategyRegistry<>(strategyYaml));
  }

  @Test
  @DisplayName("should throw NullPointerException when calling get with null key")
  void shouldThrowWhenGetWithNullKey() {
    ConfigLoader strategyJson = mock(ConfigLoader.class);
    when(strategyJson.getKeys()).thenReturn(Collections.singleton("json"));

    StrategyRegistry<String, ConfigLoader> registry = new StrategyRegistry<>(strategyJson);

    assertThrows(NullPointerException.class, () -> registry.get(null));
  }
}
