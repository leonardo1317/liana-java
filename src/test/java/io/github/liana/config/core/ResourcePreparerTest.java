package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.github.liana.config.api.Placeholder;
import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ResourcePreparerTest {

  @Mock
  private ResourceLocation resourceLocation;

  @Mock
  private ResourceNameValidator resourceNameValidator;

  private ResourcePreparer preparer;

  @BeforeEach
  void setUp() {
    preparer = new ResourcePreparer(resourceLocation, null,
        resourceNameValidator
    );
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("should throw when ResourceLocation is null")
    void shouldThrowWhenConfigResourceLocationIsNull() {
      assertThrows(NullPointerException.class,
          () -> new ResourcePreparer(null, null,
              resourceNameValidator)
      );
    }

    @Test
    @DisplayName("should use profile from environment variable when not provided explicitly")
    void shouldUseProfileFromEnvironmentWhenNotProvidedExplicitly() {
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application";
      final String RESOURCE_NAME_PATTERN = "application-default";

      ResourcePreparer preparer = new ResourcePreparer(resourceLocation,
          resourceNameValidator
      );

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
      assertTrue(
          result.stream().anyMatch(resource -> resource.resourceName().contains(PROFILE))
      );
    }

    @Test
    @DisplayName("should use default profile when profile is null")
    void shouldUseDefaultProfileWhenProfileIsNull() {
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application";
      final String RESOURCE_NAME_PATTERN = "application-default";

      ResourcePreparer preparer = new ResourcePreparer(resourceLocation, null,
          resourceNameValidator
      );

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
      assertTrue(
          result.stream().anyMatch(resource -> resource.resourceName().contains(PROFILE))
      );
    }

    @Test
    @DisplayName("should use default profile when profile is empty")
    void shouldUseDefaultProfileWhenProfileIsEmpty() {
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application";
      final String RESOURCE_NAME_PATTERN = "application-default";

      ResourcePreparer preparer = new ResourcePreparer(resourceLocation, "",
          resourceNameValidator);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
      assertTrue(
          result.stream().anyMatch(resource -> resource.resourceName().contains(PROFILE))
      );
    }

    @Test
    @DisplayName("should assign profile correctly when profile is provided")
    void shouldAssignProfileCorrectlyWhenProfileIsProvided() {
      final String PROFILE = "dev";
      final String RESOURCE_NAME = "application";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE;

      ResourcePreparer preparer = new ResourcePreparer(resourceLocation, PROFILE,
          resourceNameValidator);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
      assertTrue(
          result.stream().anyMatch(resource -> resource.resourceName().contains(PROFILE))
      );
    }

    @Test
    @DisplayName("should throw when ResourceNameValidator is null")
    void shouldThrowWhenFilenameValidatorIsNull() {
      assertThrows(NullPointerException.class,
          () -> new ResourcePreparer(resourceLocation, null,
              null));
    }
  }

  @Nested
  @DisplayName("Provider Tests")
  class ProviderTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should use default 'classpath' when provider is null or empty")
    void shouldUseDefaultProviderWhenProviderIsNullOrEmpty(String invalidProvider) {
      final String DEFAULT_PROVIDER = "classpath";
      when(resourceLocation.provider()).thenReturn(invalidProvider);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> DEFAULT_PROVIDER.equals(resource.provider())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("should use default 'classpath' when provider contains only whitespace")
    void shouldUseDefaultProviderWhenProviderIsBlank(String provider) {
      final String DEFAULT_PROVIDER = "classpath";
      when(resourceLocation.provider()).thenReturn(provider);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> DEFAULT_PROVIDER.equals(resource.provider())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"git", "classpath", "github"})
    @DisplayName("should use provided provider when not blank")
    void shouldUseProvidedProviderWhenNotBlank(String provider) {
      final String RESOURCE_NAME = "config/application.properties";
      when(resourceLocation.provider()).thenReturn(provider);
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(Set.of(RESOURCE_NAME)));
      when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> provider.equals(resource.provider())));
    }
  }

  @Nested
  @DisplayName("ResourceNames Tests")
  class ResourceNamesTests {

    @ParameterizedTest
    @NullSource
    @DisplayName("should use default resource names when provided name is null")
    void shouldUseDefaultResourceNamesWhenProvidedNameIsNull(String resourceName) {
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE;
      var resourceNames = new HashSet<String>();
      resourceNames.add(resourceName);
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should use default resource names when provided name is blank")
    void shouldUseDefaultResourceNamesWhenProvidedNameIsBlank(String resourceName) {
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE;
      var resourceNames = new HashSet<String>();
      resourceNames.add(resourceName);
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
    }

    @Test
    @DisplayName("should resolve resource name when valid names is provided")
    void shouldResolveResourceNameWhenValidNamesIsProvided() {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME = "application." + EXTENSION;
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;

      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("application.properties");
      resourceNames.add("application-${profile}.properties");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
    }

    @Test
    @DisplayName("should only include valid resource names")
    void shouldOnlyIncludeValidResourceNames() {
      final String VALID_RESOURCE_NAME = "config/application.properties";
      final String INVALID_RESOURCE_NAME = " ../invalid.json";
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add(VALID_RESOURCE_NAME);
      resourceNames.add(INVALID_RESOURCE_NAME);

      when(resourceLocation.provider()).thenReturn("classpath");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceNameValidator.isSafeResourceName(eq(VALID_RESOURCE_NAME))).thenReturn(true);
      when(resourceNameValidator.isSafeResourceName(eq(INVALID_RESOURCE_NAME))).thenReturn(false);

      List<ResourceIdentifier> result = preparer.prepare();

      assertEquals(1, result.size());
      assertTrue(result.stream()
          .anyMatch(resource -> VALID_RESOURCE_NAME.equals(resource.resourceName())));
    }

    @Test
    @DisplayName("should return an empty list when resource names are invalid and default provider is used")
    void shouldReturnAnEmptyListWhenResourceNamesAreInvalidAndDefaultProviderIsUsed() {
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("../application/${profile}.properties");
      resourceNames.add("../config../.json");
      when(resourceLocation.provider()).thenReturn("classpath");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return an empty list when resource names are invalid and non-default provider is used")
    void shouldReturnAnEmptyListWhenResourceNamesAreInvalidAndNonDefaultProviderIsUsed() {
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("/application/${profile}.properties");
      resourceNames.add("../config../.json");
      when(resourceLocation.provider()).thenReturn("git");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("should return an empty list when resource names are null or empty with a non-default provider")
    void shouldReturnAnEmptyListWhenResourceNamesAreNullOrEmptyWithANonDefaultProvider(
        String resourceName) {
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add(resourceName);
      when(resourceLocation.provider()).thenReturn("git");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("should return an empty list when resource names are blank with a non-default provider")
    void shouldReturnAnEmptyListWhenResourceNamesAreBlankWithANonDefaultProvider(
        String resourceName) {
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add(resourceName);
      when(resourceLocation.provider()).thenReturn("git");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Variables Tests")
  class VariablesTests {

    @Test
    @DisplayName("should use default profile variable when provider is default and variables are null")
    void shouldAddDefaultProfileVariableWhenDefaultProviderAndVariablesNull() {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("application-${profile}.properties");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceLocation.variables()).thenReturn(null);
      when(resourceLocation.placeholder()).thenReturn(Placeholder.builder().build());
      when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
    }

    @Test
    @DisplayName("should use default profile variable when provider is default and variables are empty")
    void shouldAddDefaultProfileVariableWhenDefaultProviderAndVariablesEmpty() {
      final String EXTENSION = "properties";
      final String PROFILE = "default";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("application-${profile}.properties");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceLocation.variables()).thenReturn(ImmutableConfigMap.empty());
      when(resourceLocation.placeholder()).thenReturn(Placeholder.builder().build());
      when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
    }

    @Test
    @DisplayName("should use provided variables when provider is not default")
    void shouldReturnProvidedVariablesWhenIsNotDefaultProvider() {
      final String EXTENSION = "properties";
      final String PROFILE = "dev";
      final String RESOURCE_NAME = "application-" + PROFILE + "." + EXTENSION;
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("application-${profile}.properties");
      when(resourceLocation.provider()).thenReturn("git");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceLocation.variables()).thenReturn(
          ImmutableConfigMap.of(Map.of("profile", PROFILE)));
      when(resourceLocation.placeholder()).thenReturn(Placeholder.builder().build());
      when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
    }

    @Test
    @DisplayName("should return empty resource name when profile variable is missing")
    void shouldSkipResourceWhenVariablesMissingForInterpolation() {
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("application-${profile}.properties");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceLocation.variables()).thenReturn(
          ImmutableConfigMap.of(Map.of("env", "dev")));
      when(resourceLocation.placeholder()).thenReturn(Placeholder.builder().build());

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should resolve resource name using profile variable when pattern is valid")
    void shouldResolveResourceNameAndExtensionWhenValid() {
      final String EXTENSION = "properties";
      final String PROFILE = "test";
      final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
      var resourceNames = new LinkedHashSet<String>();
      resourceNames.add("application-${profile}.properties");
      when(resourceLocation.resourceNames()).thenReturn(
          ImmutableConfigSet.of(resourceNames));
      when(resourceLocation.variables()).thenReturn(
          ImmutableConfigMap.of(Map.of("profile", PROFILE)));
      when(resourceLocation.placeholder()).thenReturn(Placeholder.builder().build());
      when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

      List<ResourceIdentifier> result = preparer.prepare();

      assertTrue(result.stream()
          .anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.resourceName())));
    }
  }

  @Test
  @DisplayName("should extract provider and name when resourceName contains provider prefix")
  void shouldExtractProviderFromEmbeddedResourceName() {
    var provider = "file";
    final String RESOURCE_NAME = "application.properties";
    var resourceNames = new LinkedHashSet<String>();
    resourceNames.add("file:application.properties");
    when(resourceLocation.resourceNames()).thenReturn(ImmutableConfigSet.of(resourceNames));
    when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

    List<ResourceIdentifier> result = preparer.prepare();

    assertTrue(result.stream()
        .anyMatch(resource -> RESOURCE_NAME.equals(resource.resourceName())));
    assertTrue(result.stream()
        .anyMatch(resource -> provider.equals(resource.provider())));
  }

  @Test
  @DisplayName("should use global provider when resourceName has no provider, and override when it has")
  void shouldUseGlobalProviderWhenNoEmbeddedAndOverrideWhenEmbedded() {
    final String GLOBAL_PROVIDER = "classpath";
    final String EMBEDDED_PROVIDER = "file";

    var resourceNames = new LinkedHashSet<String>();
    resourceNames.add("file:application.properties");
    resourceNames.add("config/application.yaml");

    when(resourceLocation.provider()).thenReturn(GLOBAL_PROVIDER);
    when(resourceLocation.resourceNames()).thenReturn(ImmutableConfigSet.of(resourceNames));
    when(resourceNameValidator.isSafeResourceName(anyString())).thenReturn(true);

    List<ResourceIdentifier> result = preparer.prepare();

    assertTrue(result.stream()
        .anyMatch(resource -> "application.properties".equals(resource.resourceName()) &&
            EMBEDDED_PROVIDER.equals(resource.provider())));

    assertTrue(result.stream()
        .anyMatch(resource -> "config/application.yaml".equals(resource.resourceName()) &&
            GLOBAL_PROVIDER.equals(resource.provider())));
  }
}
