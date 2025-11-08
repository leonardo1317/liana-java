package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceExtensionResolverTest {

  @Mock
  private ResourceLocator resourceLocator;
  private ResourceExtensionResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new ResourceExtensionResolver(new LinkedHashSet<>(List.of("yaml", "json")),
        resourceLocator);
  }

  @Test
  @DisplayName("should return true when resource has allowed extension")
  void shouldReturnTrueWhenResourceHasAllowedExtension() {
    assertTrue(resolver.isExtensionAllowed("config/settings.yaml"));
  }

  @Test
  @DisplayName("should return false when resource has disallowed extension")
  void shouldReturnFalseWhenResourceHasDisallowedExtension() {
    assertFalse(resolver.isExtensionAllowed("config/settings.xml"));
  }

  @ParameterizedTest()
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should return false when resource name is blank")
  void shouldReturnFalseWhenResourceNameIsBlank(String resourceName) {
    assertFalse(resolver.isExtensionAllowed(resourceName));
  }

  @Test
  @DisplayName("should return resource when it has allowed extension and exists")
  void shouldReturnResourceWhenExtensionAllowedAndExists() {
    String resourceName = "config/settings.yaml";
    when(resourceLocator.resourceExists(resourceName)).thenReturn(true);

    Optional<String> result = resolver.findConfigResource(resourceName);

    assertTrue(result.isPresent());
    assertEquals(resourceName, result.get());
  }

  @Test
  @DisplayName("should return empty when extension is allowed but resource not found")
  void shouldReturnEmptyWhenAllowedExtensionButNotFound() {
    String resourceName = "config/settings.yaml";
    when(resourceLocator.resourceExists(resourceName)).thenReturn(false);

    Optional<String> result = resolver.findConfigResource(resourceName);

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should return empty when extension is not allowed")
  void shouldReturnEmptyWhenExtensionNotAllowed() {
    Optional<String> result = resolver.findConfigResource("config/settings.xml");

    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should return matching resource when no extension provided and file exists")
  void shouldReturnMatchingResourceWhenNoExtensionProvidedAndFileExists() {
    String resourceName = "config/settings.yaml";
    when(resourceLocator.resourceExists(resourceName)).thenReturn(true);

    Optional<String> result = resolver.findConfigResource("config/settings");

    assertTrue(result.isPresent());
    assertEquals(resourceName, result.get());
  }

  @Test
  @DisplayName("should return empty when no extension provided and none of the files exist")
  void shouldReturnEmptyWhenNoExtensionProvidedAndFilesNotFound() {
    when(resourceLocator.resourceExists("config/settings.yaml")).thenReturn(false);
    when(resourceLocator.resourceExists("config/settings.json")).thenReturn(false);

    Optional<String> result = resolver.findConfigResource("config/settings");

    assertTrue(result.isEmpty());
  }

  @ParameterizedTest()
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should return empty when resource name is blank")
  void shouldReturnEmptyWhenResourceNameBlank(String resourceName) {
    Optional<String> result = resolver.findConfigResource(resourceName);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should handle resource names with mixed-case extensions")
  void shouldHandleResourceNamesWithMixedCaseExtensions() {
    String resourceName = "config/settings.YAML";
    when(resourceLocator.resourceExists(resourceName)).thenReturn(true);

    Optional<String> result = resolver.findConfigResource(resourceName);

    assertTrue(result.isPresent());
    assertEquals(resourceName, result.get());
  }
}
