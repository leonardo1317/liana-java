package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import io.github.liana.config.api.Placeholder;
import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultPipelineTest {

  @Mock
  private ProvidersRegistry providers;

  @Mock
  private LoadersRegistry loaders;

  @Mock
  private JacksonMerger merger;

  @Mock
  private JacksonInterpolator interpolator;

  @Mock
  private ResourceLocation location;

  @Test
  @DisplayName("constructor should throw NullPointerException when providers is null")
  void shouldThrowWhenProvidersIsNull() {
    assertThrows(
        NullPointerException.class,
        () -> new DefaultPipeline(null, loaders, merger, interpolator)
    );
  }

  @Test
  @DisplayName("constructor should throw NullPointerException when loaders is null")
  void shouldThrowWhenLoadersIsNull() {
    assertThrows(
        NullPointerException.class,
        () -> new DefaultPipeline(providers, null, merger, interpolator)
    );
  }

  @Test
  @DisplayName("constructor should throw NullPointerException when merger is null")
  void shouldThrowWhenMergerIsNull() {
    assertThrows(
        NullPointerException.class,
        () -> new DefaultPipeline(providers, loaders, null, interpolator)
    );
  }

  @Test
  @DisplayName("constructor should throw NullPointerException when interpolator is null")
  void shouldThrowWhenInterpolatorIsNull() {
    assertThrows(
        NullPointerException.class,
        () -> new DefaultPipeline(providers, loaders, merger, null)
    );
  }

  @Test
  @DisplayName("should throw NullPointerException when location is null")
  void shouldThrowWhenLocationIsNull() {
    var pipeline = new DefaultPipeline(providers, loaders, merger, interpolator);
    assertThrows(NullPointerException.class, () -> pipeline.execute(null));
  }

  @Test
  @DisplayName("should load, merge, and interpolate configuration successfully")
  void shouldExecutePipelineSuccessfully() {
    var raw = List.<Map<String, Object>>of(Map.of("port", 8080));
    var merged = Map.<String, Object>of("port", 8080);
    var finalResult = Map.<String, Object>of("port", 8080);

    var placeholder = mock(Placeholder.class);

    when(location.baseDirectories()).thenReturn(ImmutableConfigSet.of(Set.of("config")));
    when(location.variables()).thenReturn(ImmutableConfigMap.empty());
    when(location.placeholder()).thenReturn(placeholder);

    try (var construction = mockConstruction(ResourceProcessor.class,
        (mock, context) -> when(mock.load(location)).thenReturn(raw))) {

      when(merger.merge(raw)).thenReturn(merged);
      when(interpolator.interpolate(eq(merged), eq(placeholder), eq(ImmutableConfigMap.empty())))
          .thenReturn(finalResult);

      var pipeline = new DefaultPipeline(providers, loaders, merger, interpolator);
      var result = pipeline.execute(location);

      assertEquals(finalResult, result);

      InOrder order = inOrder(merger, interpolator);
      order.verify(merger).merge(raw);
      order.verify(interpolator).interpolate(eq(merged), eq(placeholder), eq(ImmutableConfigMap.empty()));

      assertEquals(1, construction.constructed().size());
    }
  }

  @Test
  @DisplayName("should propagate exceptions thrown by ResourceProcessor.load")
  void shouldPropagateProcessorException() {
    try (MockedConstruction<ResourceProcessor> ignored =
        mockConstruction(ResourceProcessor.class,
            (mock, context) -> when(mock.load(location))
                .thenThrow(new RuntimeException("load failed")))) {

      var pipeline = new DefaultPipeline(providers, loaders, merger, interpolator);

      assertThrows(RuntimeException.class, () -> pipeline.execute(location));
    }
  }
}
