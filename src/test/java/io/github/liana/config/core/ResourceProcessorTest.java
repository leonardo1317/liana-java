package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Configuration;
import io.github.liana.config.internal.ImmutableConfigSet;
import io.github.liana.config.spi.ResourceLoader;
import io.github.liana.config.spi.ResourceProvider;
import java.util.ArrayList;
import java.util.List;
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
class ResourceProcessorTest {

  @Mock
  private ProvidersRegistry providers;

  @Mock
  private LoadersRegistry loaders;

  @Mock
  private LoadingCache<ImmutableConfigSet, StrategyResolver<String, ResourceProvider>> providerCache;

  @Mock
  private LoadingCache<String, StrategyResolver<String, ResourceLoader>> loaderCache;

  @Mock
  private ResourcePreparer resourcePreparer;

  @Mock
  private ResourceLocation location;

  @Mock
  private ResourceProvider provider;

  @Mock
  private ResourceLoader loader;

  @Mock
  private DefaultResourceIdentifier resourceIdentifier;

  @Mock
  private DefaultResourceStream resource;

  @Mock
  private StrategyResolver<String, ResourceProvider> providerResolver;

  @Mock
  private StrategyResolver<String, ResourceLoader> loaderResolver;

  @Mock
  private Configuration configuration;

  private ResourceProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ResourceProcessor(providers, loaders, resourcePreparer);
  }

  @Test
  @DisplayName("should throw NullPointerException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ResourceProcessor(null, loaders, resourcePreparer, providerCache,
            loaderCache));
  }

  @Test
  @DisplayName("should throw NullPointerException when loader is null")
  void shouldThrowWhenLoaderIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ResourceProcessor(providers, null, resourcePreparer, providerCache,
            loaderCache));
  }

  @Test
  @DisplayName("should throw NullPointerException when resourcePreparer is null")
  void shouldThrowWhenConfigResourcePreparerIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ResourceProcessor(providers, loaders, null, providerCache, loaderCache));
  }

  @Test
  @DisplayName("should throw NullPointerException when providerCache is null")
  void shouldThrowWhenProviderCacheIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ResourceProcessor(providers, loaders, resourcePreparer, null,
            loaderCache));
  }

  @Test
  @DisplayName("should throw NullPointerException when loaderCache is null")
  void shouldThrowWhenLoaderCacheIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ResourceProcessor(providers, loaders, resourcePreparer, providerCache,
            null));
  }

  @Test
  @DisplayName("should throw NullPointerException when location is null")
  void shouldThrowExceptionWhenLocationIsNull() {
    assertThrows(NullPointerException.class, () -> processor.load(null));
  }

  @Test
  @DisplayName("should return empty list when preparer returns a list containing a null identifier")
  void shouldReturnEmptyWhenIdentifierInListIsNull() {
    List<ResourceIdentifier> identifiers = new ArrayList<>();
    identifiers.add(null);

    when(resourcePreparer.prepare()).thenReturn(identifiers);

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should skip identifier when provider is blank")
  void shouldSkipWhenProviderBlank() {
    when(location.verboseLogging()).thenReturn(true);
    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("  ");

    List<Map<String, Object>> result = processor.load(location);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should skip when resourceName is blank")
  void shouldSkipWhenResourceNameBlank() {
    when(location.verboseLogging()).thenReturn(true);
    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("");

    List<Map<String, Object>> result = processor.load(location);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should call providerCache.getOrCompute and loaderCache.getOrCompute and cache their values")
  void shouldUseProviderAndLoaderCache() throws Exception {
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("config"));
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(true);
    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");
    when(provider.getKeys()).thenReturn(Set.of("classpath"));
    StrategyRegistry<String, ResourceProvider> providerRegistry =
        new StrategyRegistry<>(provider);
    when(providers.create(anyCollection())).thenReturn(providerRegistry);
    when(provider.resolveResource(resourceIdentifier)).thenReturn(resource);
    when(resource.name()).thenReturn("app.yaml");
    when(providerCache.getOrCompute(eq(dirs), any()))
        .thenAnswer(
            inv -> inv.<Supplier<StrategyResolver<String, ResourceProvider>>>getArgument(1).get());
    when(loader.getKeys()).thenReturn(Set.of("yaml"));
    StrategyRegistry<String, ResourceLoader> loaderRegistry =
        new StrategyRegistry<>(loader);
    when(loaders.create()).thenReturn(loaderRegistry);
    when(loaderCache.getOrCompute(eq("default"), any()))
        .thenAnswer(
            inv -> inv.<Supplier<StrategyResolver<String, ResourceLoader>>>getArgument(1).get());
    when(loader.load(resource)).thenReturn(configuration);
    when(configuration.getRootAsMap()).thenReturn(Map.of());

    processor.load(location);

    verify(providerCache, times(1)).getOrCompute(eq(dirs), any());
    verify(loaderCache, times(1)).getOrCompute(eq("default"), any());
    verify(resource, times(1)).close();
  }

  @Test
  @DisplayName("should NOT recreate resolvers when value already cached")
  void shouldNotRecreateResolversWhenCacheHits() throws Exception {
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("base"));
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(true);

    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");

    when(providerCache.getOrCompute(eq(dirs), any()))
        .thenReturn(providerResolver);

    when(providerResolver.resolve("classpath")).thenReturn(provider);
    when(provider.resolveResource(resourceIdentifier)).thenReturn(resource);
    when(resource.name()).thenReturn("app.yaml");

    when(loaderCache.getOrCompute(eq("default"), any()))
        .thenReturn(loaderResolver);

    when(loaderResolver.resolve("yaml")).thenReturn(loader);
    when(loader.load(resource)).thenReturn(configuration);
    when(configuration.getRootAsMap()).thenReturn(Map.of());

    processor.load(location);
    processor.load(location);

    verify(providerCache, times(2)).getOrCompute(eq(dirs), any());
    verify(providerResolver, times(2)).resolve("classpath");
    verify(resource, times(2)).close();
  }

  @Test
  @DisplayName("should return empty list when providerResolver fails to resolve provider")
  void shouldReturnEmptyWhenProviderResolverFails() {
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("config"));
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(true);
    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("file");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");
    when(provider.getKeys()).thenReturn(Set.of("classpath"));
    StrategyRegistry<String, ResourceProvider> providerRegistry =
        new StrategyRegistry<>(provider);
    when(providers.create(anyCollection())).thenReturn(providerRegistry);
    when(providerCache.getOrCompute(eq(dirs), any()))
        .thenAnswer(
            inv -> inv.<Supplier<StrategyResolver<String, ResourceProvider>>>getArgument(1).get());
    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should return empty list when loaderResolver fails to resolve provider")
  void shouldReturnEmptyWhenLoaderResolverFails() throws Exception {
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("config"));
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(true);
    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");
    when(provider.getKeys()).thenReturn(Set.of("classpath"));

    StrategyRegistry<String, ResourceProvider> providerRegistry =
        new StrategyRegistry<>(provider);
    when(providers.create(anyCollection())).thenReturn(providerRegistry);
    when(resource.name()).thenReturn("app.yaml");
    when(provider.resolveResource(resourceIdentifier)).thenReturn(resource);
    when(providerCache.getOrCompute(eq(dirs), any()))
        .thenAnswer(
            inv -> inv.<Supplier<StrategyResolver<String, ResourceProvider>>>getArgument(1).get());

    when(loader.getKeys()).thenReturn(Set.of("Json"));
    StrategyRegistry<String, ResourceLoader> loaderRegistry =
        new StrategyRegistry<>(loader);
    when(loaders.create()).thenReturn(loaderRegistry);
    when(loaderCache.getOrCompute(eq("default"), any()))
        .thenAnswer(
            inv -> inv.<Supplier<StrategyResolver<String, ResourceLoader>>>getArgument(1).get());

    processor.load(location);

    verify(providerCache, times(1)).getOrCompute(eq(dirs), any());
    verify(loaderCache, times(1)).getOrCompute(eq("default"), any());
    verify(resource, times(1)).close();
  }


  @Test
  @DisplayName("should return empty when resolveResource throws")
  void shouldHandleExceptionOnResolveResource() {
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("base"));
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(false);

    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");

    when(providerCache.getOrCompute(eq(dirs), any()))
        .thenReturn(providerResolver);

    when(providerResolver.resolve("classpath")).thenReturn(provider);
    when(provider.resolveResource(resourceIdentifier))
        .thenThrow(new RuntimeException("resolve failed"));

    List<Map<String, Object>> result = processor.load(location);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should close resource when loader throws unexpected exception")
  void shouldCloseResourceOnUnexpectedLoaderException() throws Exception {
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("config"));
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(false);

    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));

    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");

    when(providerCache.getOrCompute(eq(dirs), any())).thenReturn(providerResolver);
    when(providerResolver.resolve("classpath")).thenReturn(provider);

    when(provider.resolveResource(resourceIdentifier)).thenReturn(resource);
    when(resource.name()).thenReturn("app.yaml");

    when(loaderCache.getOrCompute(eq("default"), any())).thenReturn(loaderResolver);
    when(loaderResolver.resolve("yaml")).thenReturn(loader);

    when(loader.load(resource)).thenThrow(new RuntimeException("unknown error"));

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
    verify(resource, times(1)).close();
  }

  @Test
  @DisplayName("should return unmodifiable list")
  void shouldReturnUnmodifiableList() {
    when(location.verboseLogging()).thenReturn(false);
    when(resourcePreparer.prepare()).thenReturn(List.of());

    List<Map<String, Object>> result = processor.load(location);

    assertThrows(UnsupportedOperationException.class, () -> result.add(Map.of()));
  }

  @Test
  @DisplayName("should return empty when provider returns a null resource")
  void shouldReturnEmptyWhenResourceIsNull() {
    var processor = new ResourceProcessor(providers, loaders, resourcePreparer,
        providerCache, loaderCache);
    ImmutableConfigSet dirs = ImmutableConfigSet.of(Set.of("base"));
    when(location.baseDirectories()).thenReturn(dirs);
    when(location.verboseLogging()).thenReturn(false);

    when(resourcePreparer.prepare()).thenReturn(List.of(resourceIdentifier));
    when(resourceIdentifier.provider()).thenReturn("classpath");
    when(resourceIdentifier.resourceName()).thenReturn("app.yaml");

    when(providerCache.getOrCompute(eq(dirs), any()))
        .thenReturn(providerResolver);

    when(providerResolver.resolve("classpath")).thenReturn(provider);
    when(provider.resolveResource(resourceIdentifier)).thenReturn(null);

    List<Map<String, Object>> result = processor.load(location);
    assertTrue(result.isEmpty());
    verify(loaders, never()).create();
  }

}
