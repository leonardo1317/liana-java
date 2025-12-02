package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Configuration;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultConfigurationManagerTest {

  @Mock
  private LoadingCache<ResourceLocation, Map<String, Object>> cache;

  @Mock
  private Pipeline pipeline;

  @Mock
  private ResourceLocation location;

  private DefaultConfigurationManager manager;

  @BeforeEach
  void setUp() {
    manager = new DefaultConfigurationManager(cache, pipeline);
  }

  @Test
  @DisplayName("convenience constructor should create instance correctly")
  void shouldConstructUsingConvenienceCtor() {
    DefaultConfigurationManager mgr = new DefaultConfigurationManager(pipeline);
    assertNotNull(mgr);
  }

  @Test
  @DisplayName("should throw when cache is null")
  void shouldThrowWhenCacheIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigurationManager(null, pipeline));
  }

  @Test
  @DisplayName("should throw when pipeline is null")
  void shouldThrowWhenPipelineIsNull() {
    assertThrows(NullPointerException.class,
        () -> new DefaultConfigurationManager(cache, null));
  }

  @Test
  @DisplayName("should throw NullPointerException when location is null")
  void shouldThrowWhenLocationIsNull() {
    assertThrows(NullPointerException.class, () -> manager.load(null));
  }

  @Test
  @DisplayName("should load configuration from cache when available")
  void shouldLoadFromCacheWhenAvailable() {
    Map<String, Object> cached = Map.of("port", 8080);

    when(cache.getOrCompute(eq(location), any())).thenReturn(cached);

    Configuration config = manager.load(location);

    assertNotNull(config);
    assertEquals(cached, config.getRootAsMap());

    verify(cache).getOrCompute(eq(location), any());
    verifyNoInteractions(pipeline);
  }

  @Test
  @DisplayName("should compute configuration using pipeline when not cached")
  void shouldComputeConfigurationWhenNotCached() {
    Map<String, Object> pipelineResult = Map.of("name", "resolved");

    when(cache.getOrCompute(eq(location), any()))
        .thenAnswer(inv -> inv.<Supplier<Map<String, Object>>>getArgument(1).get());

    when(pipeline.execute(location)).thenReturn(pipelineResult);

    Configuration config = manager.load(location);

    assertEquals(pipelineResult, config.getRootAsMap());

    verify(pipeline).execute(location);
  }

  @Test
  @DisplayName("should propagate exception thrown by cache supplier")
  void shouldPropagateExceptionFromCacheSupplier() {
    when(cache.getOrCompute(eq(location), any()))
        .thenThrow(new RuntimeException("cache failure"));

    assertThrows(RuntimeException.class, () -> manager.load(location));
  }
}
