package io.github.liana.config;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigLoaderException;
import io.github.liana.config.exception.ConfigProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigResourceProcessorTest {

  @Mock
  private ConfigResourceProvider provider;

  @Mock
  private ConfigResourceLoader loader;

  @Mock
  private ConfigResourceLocation location;

  @Mock
  private ConfigResourceReference reference;

  @Mock
  private ConfigResource resource;

  @Mock
  private Configuration configuration;

  private ConfigResourceProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ConfigResourceProcessor(provider, loader);
  }

  @Test
  @DisplayName("should throw NullPointerException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ConfigResourceProcessor(null, loader));
  }

  @Test
  @DisplayName("should throw NullPointerException when loader is null")
  void shouldThrowWhenLoaderIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ConfigResourceProcessor(provider, null));
  }

  @Test
  @DisplayName("should throw NullPointerException when location is null")
  void shouldThrowExceptionWhenLocationIsNull() {
    assertThrows(NullPointerException.class, () -> processor.load(null));
  }

  @Test
  @DisplayName("should return empty list when no references are prepared")
  void shouldReturnEmptyListWhenNoReferencesPrepared() {
    when(location.isVerboseLogging()).thenReturn(false);

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(emptyList())
    )) {
      List<Map<String, Object>> result = processor.load(location);

      assertTrue(result.isEmpty());
      assertThrows(UnsupportedOperationException.class, () -> result.add(Map.of()));
    }
  }

  @Test
  @DisplayName("should load configuration successfully for valid reference")
  void shouldLoadConfigurationSuccessfullyForValidReference() {
    Map<String, Object> configData = Map.of("app.name", "my-app");
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");
    when(provider.resolve(reference)).thenReturn(resource);
    when(loader.loadFromResource(resource)).thenReturn(configuration);
    when(configuration.getRootAsMap()).thenReturn(configData);

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(List.of(reference))
    )) {
      List<Map<String, Object>> result = processor.load(location);

      assertEquals(1, result.size());
      assertEquals(configData, result.get(0));
      verify(provider).resolve(reference);
      verify(loader).loadFromResource(resource);
    }
  }

  @Test
  @DisplayName("should skip null references and process only valid references")
  void shouldSkipNullReferencesAndProcessValidOnes() {
    when(location.isVerboseLogging()).thenReturn(true);

    ConfigResourceReference validRef = mock(ConfigResourceReference.class);
    when(validRef.provider()).thenReturn("file");
    when(validRef.resourceName()).thenReturn("prod.yaml");

    List<ConfigResourceReference> references = new ArrayList<>();
    references.add(null);
    references.add(validRef);

    Map<String, Object> expectedConfig = Map.of("app.name", "my-app");
    when(provider.resolve(validRef)).thenReturn(resource);
    when(loader.loadFromResource(resource)).thenReturn(configuration);
    when(configuration.getRootAsMap()).thenReturn(expectedConfig);

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(references)
    )) {

      List<Map<String, Object>> result = processor.load(location);
      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals(expectedConfig, result.get(0));
      verify(provider, times(1)).resolve(validRef);
      verify(loader, times(1)).loadFromResource(resource);
      verifyNoMoreInteractions(provider, loader);
    }
  }

  @Test
  @DisplayName("should skip null references")
  void shouldSkipNullReferences() {
    when(location.isVerboseLogging()).thenReturn(true);

    var references = new ArrayList<ConfigResourceReference>();
    references.add(null);

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(references)
    )) {
      List<Map<String, Object>> result = processor.load(location);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  @DisplayName("should skip invalid reference when provider is blank")
  void shouldSkipInvalidReferenceWhenBlankProvider() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("");

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(List.of(reference))
    )) {
      List<Map<String, Object>> result = processor.load(location);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  @DisplayName("should skip invalid reference when resource name is blank")
  void shouldSkipInvalidReferenceWhenBlankResourceName() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("");

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(List.of(reference))
    )) {
      List<Map<String, Object>> result = processor.load(location);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  @DisplayName("should skip and log when provider resolution fails")
  void shouldSkipWhenProviderResolutionFails() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");
    when(provider.resolve(reference)).thenThrow(new ConfigProviderException("provider fail"));

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(List.of(reference))
    )) {
      List<Map<String, Object>> result = processor.load(location);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  @DisplayName("should skip when loader fails to load configuration")
  void shouldSkipWhenLoaderFails() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");

    when(provider.resolve(reference)).thenReturn(resource);
    when(loader.loadFromResource(resource)).thenThrow(new ConfigLoaderException("load fail"));

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(List.of(reference))
    )) {
      List<Map<String, Object>> result = processor.load(location);
      assertTrue(result.isEmpty());
    }
  }

  @Test
  @DisplayName("should handle unexpected exceptions gracefully")
  void shouldHandleUnexpectedExceptionsGracefully() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");

    when(provider.resolve(reference)).thenThrow(new RuntimeException("unexpected error"));

    try (MockedConstruction<ConfigResourcePreparer> ignored = mockConstruction(
        ConfigResourcePreparer.class,
        (mockPreparer, context) -> when(mockPreparer.prepare()).thenReturn(List.of(reference))
    )) {
      List<Map<String, Object>> result = processor.load(location);
      assertTrue(result.isEmpty());
    }
  }
}
