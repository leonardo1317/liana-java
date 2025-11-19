package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
  private JacksonMerger merger;

  @Mock
  private JacksonInterpolator interpolator;

  @Mock
  private ConfigResourceLocation location;

  @Mock
  private ProvidersRegistry providersRegistry;

  @Mock
  private LoadersRegistry loadersRegistry;

  private ConfigManager manager;

  @BeforeEach
  void setUp() {
    manager = new DefaultConfigManager(cache, providersRegistry, loadersRegistry, merger,
        interpolator);
  }

  @Test
  @DisplayName("convenience constructor creates instance and delegates correctly")
  void shouldConstructUsingConvenienceCtor() {
    DefaultConfigManager mgr = new DefaultConfigManager(providersRegistry, loadersRegistry, merger, interpolator);
    assertNotNull(mgr);
  }

  @Test
  @DisplayName("should throw NullPointerException when cache is null")
  void shouldThrowWhenCacheIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(null, providersRegistry, loadersRegistry, merger,
            interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when providersRegistry is null")
  void shouldThrowWhenDefaultProvidersIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, null, loadersRegistry, merger, interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when loadersRegistry is null")
  void shouldThrowWhenDefaultLoadersIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, providersRegistry, null, merger, interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when merger is null")
  void shouldThrowWhenMergerIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, providersRegistry, loadersRegistry, null,
            interpolator));
  }

  @Test
  @DisplayName("should throw NullPointerException when interpolator is null")
  void shouldThrowWhenInterpolatorIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigManager(cache, providersRegistry, loadersRegistry, merger, null));
  }

  @Test
  @DisplayName("should throw NullPointerException when location is null")
  void shouldThrowWhenLocationIsNull() {
    assertThrows(NullPointerException.class, () -> manager.load(null));
  }

  @Test
  @DisplayName("should load configuration from cache when available")
  void shouldLoadFromCacheWhenAvailable() {
    final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);
    Map<String, Object> cachedMap = Map.of("key", "value");

    ConfigProvider configProvider = mock(ConfigProvider.class);
    List<ConfigProvider> providers = List.of(configProvider);
    var providerRegistry = new StrategyRegistry<>(keyNormalizer, providers);

    ConfigLoader configLoader = mock(ConfigLoader.class);
    List<ConfigLoader> loaders = List.of(configLoader);
    var loaderRegistry = new StrategyRegistry<>(keyNormalizer, loaders);
    when(providersRegistry.create(anyCollection())).thenReturn(providerRegistry);
    when(loadersRegistry.create()).thenReturn(loaderRegistry);
    when(cache.getOrCompute(eq("ALL_CONFIG"), any())).thenReturn(cachedMap);
    when(location.getBaseDirectories()).thenReturn(ImmutableConfigSet.of(Set.of("config")));
    Configuration result = manager.load(location);

    assertNotNull(result);
    verify(cache).getOrCompute(eq("ALL_CONFIG"), any());
    verifyNoInteractions(merger, interpolator);
  }

  @Test
  @DisplayName("should compute and cache configuration when not present")
  void shouldComputeConfigurationWhenNotCached() {
    final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);
    Map<String, Object> merged = Map.of("app.name", "${MY_APP}");
    Map<String, Object> interpolated = Map.of("app.name", "LianaService");

    Placeholder placeholder = mock(Placeholder.class);
    when(location.getPlaceholder()).thenReturn(placeholder);
    when(location.getVariables()).thenReturn(
        ImmutableConfigMap.of(Map.of("MY_APP", "LianaService"))
    );
    when(location.getBaseDirectories()).thenReturn(ImmutableConfigSet.of(Set.of("config")));

    ConfigProvider configProvider = mock(ConfigProvider.class);
    List<ConfigProvider> providers = List.of(configProvider);
    var providerRegistry = new StrategyRegistry<>(keyNormalizer, providers);

    ConfigLoader configLoader = mock(ConfigLoader.class);
    List<ConfigLoader> loaders = List.of(configLoader);
    var loaderRegistry = new StrategyRegistry<>(keyNormalizer, loaders);
    when(providersRegistry.create(anyCollection())).thenReturn(providerRegistry);
    when(loadersRegistry.create()).thenReturn(loaderRegistry);

    when(cache.getOrCompute(eq("ALL_CONFIG"), any())).thenAnswer(invocation -> {
      Supplier<Map<String, Object>> supplier = invocation.getArgument(1);
      return supplier.get();
    });

    when(merger.merge(anyList())).thenReturn(merged);
    when(interpolator.interpolate(eq(merged), eq(placeholder), anyMap())).thenReturn(interpolated);

    Configuration reader = manager.load(location);

    assertNotNull(reader);
    verify(merger).merge(anyList());
    verify(interpolator).interpolate(eq(merged), eq(placeholder), anyMap());
  }

  @Test
  @DisplayName("should propagate exception thrown by cache supplier")
  void shouldPropagateExceptionFromCacheSupplier() {
    final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);
    ConfigProvider configProvider = mock(ConfigProvider.class);
    List<ConfigProvider> providers = List.of(configProvider);
    var providerRegistry = new StrategyRegistry<>(keyNormalizer, providers);

    ConfigLoader configLoader = mock(ConfigLoader.class);
    List<ConfigLoader> loaders = List.of(configLoader);
    var loaderRegistry = new StrategyRegistry<>(keyNormalizer, loaders);

    when(providersRegistry.create(anyList())).thenReturn(providerRegistry);
    when(loadersRegistry.create()).thenReturn(loaderRegistry);
    when(cache.getOrCompute(eq("ALL_CONFIG"), any())).thenThrow(new RuntimeException("cache fail"));
    when(location.getBaseDirectories()).thenReturn(ImmutableConfigSet.of(Set.of("config")));
    assertThrows(RuntimeException.class, () -> manager.load(location));
  }
}
