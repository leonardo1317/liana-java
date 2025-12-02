package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.liana.config.spi.ResourceLoader;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StrategyResolverTest {

  @Mock
  private StrategyRegistry<String, Strategy<String>> registry;

  private Function<String, RuntimeException> exceptionFactory;

  private StrategyResolver<String, Strategy<String>> resolver;

  @BeforeEach
  void setUp() {
    exceptionFactory = key -> new IllegalArgumentException("Missing strategy for: " + key);
    resolver = new StrategyResolver<>(registry, exceptionFactory);
  }

  @Test
  @DisplayName("should resolve strategy when key is registered")
  void shouldResolveStrategyWhenPresent() {
    ResourceLoader yamlLoader = mock(ResourceLoader.class);

    when(registry.get("yaml")).thenReturn(Optional.of(yamlLoader));

    Strategy<String> result = resolver.resolve("yaml");

    assertSame(yamlLoader, result);
  }

  @Test
  @DisplayName("should throw exception when key is not registered")
  void shouldThrowExceptionWhenKeyNotRegistered() {
    when(registry.get("missing")).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(
        IllegalArgumentException.class,
        () -> resolver.resolve("missing")
    );

    assertEquals("Missing strategy for: missing", ex.getMessage());
  }

  @Test
  @DisplayName("should throw NullPointerException when key is null")
  void shouldThrowNullPointerExceptionWhenKeyIsNull() {
    assertThrows(NullPointerException.class, () -> resolver.resolve(null));
  }

  @Test
  @DisplayName("should throw NullPointerException when registry is null")
  void shouldThrowNullPointerExceptionWhenRegistryIsNull() {
    assertThrows(NullPointerException.class, () -> new StrategyResolver<>(null, exceptionFactory));
  }

  @Test
  @DisplayName("should throw NullPointerException when exceptionFactory is null")
  void shouldThrowNullPointerExceptionWhenExceptionFactoryIsNull() {
    assertThrows(NullPointerException.class, () -> new StrategyResolver<>(registry, null));
  }
}