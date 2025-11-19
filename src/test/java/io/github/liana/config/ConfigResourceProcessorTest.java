package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigResourceProcessorTest {

  @Mock
  private ConfigProviderResolver provider;

  @Mock
  private ConfigLoaderResolver loader;

  @Mock
  private ConfigResourceLocation location;

  @Mock
  private ConfigResourceReference reference;

  @Mock
  private ConfigLoader configLoader;

  @Mock
  private ConfigProvider configProvider;

  @Mock
  private ConfigResource resource;

  @Mock
  private Configuration configuration;

  @Mock
  private ConfigResourcePreparer configResourcePreparer;

  private ConfigResourceProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ConfigResourceProcessor(provider, loader, configResourcePreparer);
  }

  @Test
  @DisplayName("should throw NullPointerException when provider is null")
  void shouldThrowWhenProviderIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ConfigResourceProcessor(null, loader, configResourcePreparer));
  }

  @Test
  @DisplayName("should throw NullPointerException when loader is null")
  void shouldThrowWhenLoaderIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ConfigResourceProcessor(provider, null, configResourcePreparer));
  }

  @Test
  @DisplayName("should throw NullPointerException when configResourcePreparer is null")
  void shouldThrowWhenConfigResourcePreparerIsNull() {
    assertThrows(NullPointerException.class,
        () -> new ConfigResourceProcessor(provider, loader, null));
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
    when(configResourcePreparer.prepare()).thenReturn(List.of());

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> result.add(Map.of()));
  }

  @Test
  @DisplayName("should load configuration successfully for valid reference")
  void shouldLoadConfigurationSuccessfullyForValidReference() {

    Map<String, Object> configData = Map.of("app.name", "my-app");

    when(location.isVerboseLogging()).thenReturn(true);

    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");

    when(configResourcePreparer.prepare()).thenReturn(List.of(reference));
    when(provider.resolve("classpath")).thenReturn(configProvider);

    when(configProvider.resolveResource(reference)).thenReturn(resource);
    when(resource.resourceName()).thenReturn("prod.yaml");
    when(loader.resolve(anyString())).thenReturn(configLoader);

    when(configLoader.load(resource)).thenReturn(configuration);
    when(configuration.getRootAsMap()).thenReturn(configData);

    List<Map<String, Object>> result = processor.load(location);

    assertEquals(1, result.size());
    assertEquals(configData, result.get(0));

    verify(provider).resolve("classpath");
    verify(configProvider).resolveResource(reference);
    verify(loader).resolve("prod.yaml");
    verify(configLoader).load(resource);
  }

  @Test
  @DisplayName("should skip null references and process valid ones")
  void shouldSkipNullReferencesAndProcessValidOnes() {

    when(location.isVerboseLogging()).thenReturn(true);

    ConfigResourceReference validRef = mock(ConfigResourceReference.class);
    when(validRef.provider()).thenReturn("file");
    when(validRef.resourceName()).thenReturn("prod.yaml");

    List<ConfigResourceReference> references = new ArrayList<>();
    references.add(null);
    references.add(validRef);

    when(configResourcePreparer.prepare()).thenReturn(references);

    Map<String, Object> expected = Map.of("ok", true);
    when(provider.resolve("file")).thenReturn(configProvider);
    when(configProvider.resolveResource(validRef)).thenReturn(resource);
    when(resource.resourceName()).thenReturn("prod.yaml");
    when(loader.resolve(anyString())).thenReturn(configLoader);
    when(configLoader.load(resource)).thenReturn(configuration);
    when(configuration.getRootAsMap()).thenReturn(expected);

    List<Map<String, Object>> result = processor.load(location);

    assertEquals(1, result.size());
    assertEquals(expected, result.get(0));
  }


  @Test
  @DisplayName("should skip invalid reference when provider is blank")
  void shouldSkipInvalidReferenceWhenBlankProvider() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("");
    when(configResourcePreparer.prepare()).thenReturn(List.of(reference));

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should skip invalid reference when resource name is blank")
  void shouldSkipInvalidReferenceWhenBlankResourceName() {
    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("");
    when(configResourcePreparer.prepare()).thenReturn(List.of(reference));

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should skip and log when provider resolution fails")
  void shouldSkipWhenProviderResolutionFails() {

    when(location.isVerboseLogging()).thenReturn(true);

    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");

    when(configResourcePreparer.prepare()).thenReturn(List.of(reference));

    when(provider.resolve("classpath"))
        .thenThrow(new ConfigProviderException("provider fail"));

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should skip reference when ConfigLoader throws ConfigLoaderException")
  void shouldSkipWhenConfigLoaderThrowsConfigLoaderException() {

    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("app.yaml");
    when(configResourcePreparer.prepare()).thenReturn(List.of(reference));
    when(provider.resolve("classpath")).thenReturn(configProvider);
    when(configProvider.resolveResource(reference)).thenReturn(resource);
    when(resource.resourceName()).thenReturn("app.yaml");
    when(loader.resolve("app.yaml")).thenReturn(configLoader);
    when(configLoader.load(resource))
        .thenThrow(new ConfigLoaderException("failed to parse"));

    List<Map<String, Object>> result = processor.load(location);
    assertTrue(result.isEmpty());
  }


  @Test
  @DisplayName("should handle unexpected exceptions gracefully")
  void shouldHandleUnexpectedExceptionsGracefully() {

    when(location.isVerboseLogging()).thenReturn(true);
    when(reference.provider()).thenReturn("classpath");
    when(reference.resourceName()).thenReturn("prod.yaml");

    when(configResourcePreparer.prepare()).thenReturn(List.of(reference));

    when(provider.resolve("classpath"))
        .thenThrow(new RuntimeException("unexpected error"));

    List<Map<String, Object>> result = processor.load(location);

    assertTrue(result.isEmpty());
  }
}
