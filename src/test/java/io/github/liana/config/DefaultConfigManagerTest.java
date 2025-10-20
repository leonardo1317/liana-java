package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.liana.internal.ImmutableConfigMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultConfigManagerTest {

  @Mock
  private LoadingCache<String, Map<String, Object>> cache;

  @Mock
  private ConfigResourceProcessor resourceProcessor;

  @Mock
  private JacksonMerger merger;

  @Mock
  private JacksonInterpolator interpolator;

  @Mock
  private ConfigResourceLocation location;

  private ConfigManager manager;

  @BeforeEach
  void setUp() {
    manager = new DefaultConfigManager(cache, resourceProcessor, merger, interpolator);
  }

  @Test
  @DisplayName("should throw NullPointerException when cache is null")
  void shouldThrowWhenCacheIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(null, resourceProcessor, merger, interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when resourceProcessor is null")
  void shouldThrowWhenResourceProcessorIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, null, merger, interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when merger is null")
  void shouldThrowWhenMergerIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, resourceProcessor, null, interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when interpolator is null")
  void shouldThrowWhenInterpolatorIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, resourceProcessor, merger, null));
  }

  @Test
  @DisplayName("should throw NullPointerException when location is null")
  void shouldThrowWhenLocationIsNull() {
    assertThrows(NullPointerException.class, () -> manager.load(null));
  }

  @Test
  @DisplayName("should load configuration from cache when available")
  void shouldLoadFromCacheWhenAvailable() {
    Map<String, Object> cachedMap = Map.of("key", "value");
    when(cache.getOrCompute(eq("ALL_CONFIG"), any())).thenReturn(cachedMap);

    ConfigReader result = manager.load(location);

    assertNotNull(result);
    verify(cache).getOrCompute(eq("ALL_CONFIG"), any());
    verifyNoInteractions(resourceProcessor, merger, interpolator);
  }

  @Test
  @DisplayName("should compute and cache configuration when not present")
  void shouldComputeConfigurationWhenNotCached() {
    List<Map<String, Object>> loadedConfigs = List.of(
        Map.of("app.name", "${MY_APP}")
    );

    Map<String, Object> merged = Map.of("app.name", "${MY_APP}");
    Map<String, Object> interpolated = Map.of("app.name", "LianaService");
    Placeholder placeholder = mock(Placeholder.class);
    when(location.getPlaceholder()).thenReturn(placeholder);
    when(location.getVariables()).thenReturn(
        ImmutableConfigMap.of(Map.of("MY_APP", "LianaService")));
    when(resourceProcessor.load(location)).thenReturn(loadedConfigs);
    when(merger.merge(loadedConfigs)).thenReturn(merged);
    when(interpolator.interpolate(merged, placeholder, Map.of("MY_APP", "LianaService")))
        .thenReturn(interpolated);

    when(cache.getOrCompute(eq("ALL_CONFIG"), any())).thenAnswer(invocation -> {
      Supplier<Map<String, Object>> supplier = invocation.getArgument(1);
      return supplier.get();
    });

    ConfigReader reader = manager.load(location);

    assertNotNull(reader);
    verify(resourceProcessor).load(location);
    verify(merger).merge(loadedConfigs);
    verify(interpolator).interpolate(merged, placeholder, Map.of("MY_APP", "LianaService"));
  }

  @Test
  @DisplayName("should propagate exception thrown by cache supplier")
  void shouldPropagateExceptionFromCacheSupplier() {
    when(cache.getOrCompute(eq("ALL_CONFIG"), any())).thenThrow(new RuntimeException("cache fail"));
    assertThrows(RuntimeException.class, () -> manager.load(location));
  }
}
