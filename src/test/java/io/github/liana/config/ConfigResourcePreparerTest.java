package io.github.liana.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ConfigResourcePreparerTest {

    @Mock
    private ConfigResourceLocation configResourceLocation;
    private ConfigResourcePreparer preparer;

    @BeforeEach
    void setUp() {
        preparer = new ConfigResourcePreparer(configResourceLocation);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("should throw when ConfigResourceLocation is null")
        void shouldThrowWhenConfigResourceLocationIsNull() {
            assertThrows(NullPointerException.class, () -> new ConfigResourcePreparer(null));
        }

        @Test
        @DisplayName("should use default profile when profile is null")
        void shouldUseDefaultProfileWhenProfileIsNull() {
            // Given
            final String PROFILE = "default";
            ConfigResourcePreparer preparer = new ConfigResourcePreparer(configResourceLocation, null);

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> resource.getResourceName().contains(PROFILE))
            );
        }

        @Test
        @DisplayName("should use default profile when profile is empty")
        void shouldUseDefaultProfileWhenProfileIsEmpty() {
            // Given
            final String PROFILE = "default";
            ConfigResourcePreparer preparer = new ConfigResourcePreparer(configResourceLocation, "");

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> resource.getResourceName().contains(PROFILE))
            );
        }

        @Test
        @DisplayName("should assign profile correctly when profile is provided")
        void shouldAssignProfileCorrectlyWhenProfileIsProvided() {
            // Given
            final String PROFILE = "dev";
            ConfigResourcePreparer preparer = new ConfigResourcePreparer(configResourceLocation, PROFILE);

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> resource.getResourceName().contains(PROFILE))
            );
        }

    }

    @Nested
    @DisplayName("Provider Tests")
    class ProviderTests {
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should use default 'classpath' when provider is null or empty")
        void shouldUseDefaultProviderWhenProviderIsNullOrEmpty(String invalidProvider) {
            // Given
            when(configResourceLocation.getProvider()).thenReturn(invalidProvider);

            // when
            List<ResolvedConfigResource> result = preparer.prepare();

            // then
            assertEquals("classpath", result.get(0).getProvider());
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should use default 'classpath' when provider contains only whitespace")
        void shouldUseDefaultProviderWhenProviderIsBlank(String provider) {
            // Given
            when(configResourceLocation.getProvider()).thenReturn(provider);

            // when
            List<ResolvedConfigResource> result = preparer.prepare();

            // then
            assertEquals("classpath", result.get(0).getProvider());
        }

        @ParameterizedTest
        @ValueSource(strings = {"S3", "classpath", "github", "S3"})
        @DisplayName("should use provided provider when not blank")
        void shouldUseProvidedProviderWhenNotBlank(String provider) {
            // Given
            when(configResourceLocation.getProvider()).thenReturn(provider);

            // when
            List<ResolvedConfigResource> result = preparer.prepare();

            // then
            assertEquals(provider, result.get(0).getProvider());
        }
    }

    @Nested
    @DisplayName("ResourceName Tests")
    class ResourceNameTests {
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should use default resource name when provided name is null or empty")
        void shouldUseDefaultResourceNameWhenProvideIsNullOrEmpty(String resourceName) {
            // Given
            final String EXTENSION = "properties";
            final String RESOURCE_NAME = "application." + EXTENSION;
            when(configResourceLocation.getResourceName()).thenReturn(resourceName);

            try (
                    MockedStatic<FileExtensionValidator> mockedExtensions = mockStatic(FileExtensionValidator.class);
                    MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedExtensions.when(FileExtensionValidator::defaultExtensions)
                        .thenReturn(Set.of(EXTENSION));

                mockedClasspath.when(() -> ClasspathResource.resourceExists(RESOURCE_NAME))
                        .thenReturn(true);

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME))
                        .thenReturn(true);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should use default resource name when provided name is blank")
        void shouldUseDefaultResourceNameWhenProvidedNameIsBlank(String resourceName) {
            // Given
            final String EXTENSION = "properties";
            final String RESOURCE_NAME = "application." + EXTENSION;
            when(configResourceLocation.getResourceName()).thenReturn(resourceName);

            try (
                    MockedStatic<FileExtensionValidator> mockedExtensions = mockStatic(FileExtensionValidator.class);
                    MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedExtensions.when(FileExtensionValidator::defaultExtensions)
                        .thenReturn(Set.of(EXTENSION));

                mockedClasspath.when(() -> ClasspathResource.resourceExists(RESOURCE_NAME))
                        .thenReturn(true);

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME))
                        .thenReturn(true);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should set empty resource name when default name resolution fails")
        void shouldSetEmptyResourceNameWhenDefaultNameResolutionFails(String resourceName) {
            // Given
            final String EMPTY_STRING = "";
            final String EXTENSION = "properties";
            final String RESOURCE_NAME = "application." + EXTENSION;
            when(configResourceLocation.getResourceName()).thenReturn(resourceName);

            try (
                    MockedStatic<FileExtensionValidator> mockedExtensions = mockStatic(FileExtensionValidator.class);
                    MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedExtensions.when(FileExtensionValidator::defaultExtensions)
                        .thenReturn(Set.of(EXTENSION));

                mockedClasspath.when(() -> ClasspathResource.resourceExists(RESOURCE_NAME))
                        .thenReturn(false);

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> EMPTY_STRING.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"application.properties"})
        @DisplayName("should resolve resource name when valid name is provided")
        void shouldResolveResourceNameWhenValidNameIsProvided(String resourceName) {
            // Given
            final String EXTENSION = "properties";
            final String RESOURCE_NAME = "application." + EXTENSION;
            when(configResourceLocation.getResourceName()).thenReturn(resourceName);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME))
                        .thenReturn(true);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"/application/.properties", "../config../.json"})
        @DisplayName("should set empty resource name when name is invalid and default provider is used")
        void shouldSetEmptyResourceNameWhenNameIsInvalidAndDefaultProviderIsUsed(String invalidResourceName) {
            // Given
            final String EMPTY_STRING = "";
            when(configResourceLocation.getProvider()).thenReturn("classpath");
            when(configResourceLocation.getResourceName()).thenReturn(invalidResourceName);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(invalidResourceName))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> EMPTY_STRING.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"/application/.properties", "../config../.json"})
        @DisplayName("should set empty resource name when name is invalid and non-default provider is used")
        void shouldSetEmptyResourceNameWhenNameIsInvalidAndNonDefaultProviderIsUsed(String invalidResourceName) {
            // Given
            final String EMPTY_STRING = "";
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceName()).thenReturn(invalidResourceName);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(invalidResourceName))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> EMPTY_STRING.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should set empty resource name for null or empty name with non-default provider")
        void shouldSetEmptyResourceNameForNullOrEmptyNameWithNonDefaultProvider(String resourceName) {
            // Given
            final String EMPTY_STRING = "";
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceName()).thenReturn(resourceName);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {
                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(resourceName))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> EMPTY_STRING.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should set empty resource name for blank name with non-default provider")
        void shouldSetEmptyResourceNameForBlankNameWithNonDefaultProvider(String resourceName) {
            // Given
            final String EMPTY_STRING = "";
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceName()).thenReturn(resourceName);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(resourceName))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> EMPTY_STRING.equals(resource.getResourceName()))
                );
            }
        }
    }

    @Nested
    @DisplayName("ResourceNamePattern Tests")
    class ResourceNamePatternTests {
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should use default resource name pattern when provided pattern is null or empty")
        void shouldUseDefaultResourceNamePatternWhenPatternIsNullOrEmpty(String resourceNamePattern) {
            // Given
            final String EXTENSION = "properties";
            final String PROFILE = "default";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FileExtensionValidator> mockedExtensions = mockStatic(FileExtensionValidator.class);
                    MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedExtensions.when(FileExtensionValidator::defaultExtensions)
                        .thenReturn(Set.of(EXTENSION));

                mockedClasspath.when(() -> ClasspathResource.resourceExists(RESOURCE_NAME_PATTERN))
                        .thenReturn(true);

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME_PATTERN))
                        .thenReturn(true);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should use default resource name pattern when provided pattern is blank")
        void shouldUseDefaultResourceNamePatternWhenProvidedPatternIsBlank(String resourceNamePattern) {
            // Given
            final String EXTENSION = "properties";
            final String PROFILE = "default";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FileExtensionValidator> mockedExtensions = mockStatic(FileExtensionValidator.class);
                    MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedExtensions.when(FileExtensionValidator::defaultExtensions)
                        .thenReturn(Set.of(EXTENSION));

                mockedClasspath.when(() -> ClasspathResource.resourceExists(RESOURCE_NAME_PATTERN))
                        .thenReturn(true);

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME_PATTERN))
                        .thenReturn(true);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should set empty resource name when default pattern resolution fails")
        void shouldSetEmptyResourceNameWhenDefaultPatternResolutionFails(String resourceNamePattern) {
            // Given
            final String STRING_EMPTY = "";
            final String EXTENSION = "properties";
            final String PROFILE = "default";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FileExtensionValidator> mockedExtensions = mockStatic(FileExtensionValidator.class);
                    MockedStatic<ClasspathResource> mockedClasspath = mockStatic(ClasspathResource.class);
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedExtensions.when(FileExtensionValidator::defaultExtensions)
                        .thenReturn(Set.of(EXTENSION));

                mockedClasspath.when(() -> ClasspathResource.resourceExists(RESOURCE_NAME_PATTERN))
                        .thenReturn(false);

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME_PATTERN))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> STRING_EMPTY.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"application-${profile}.properties"})
        @DisplayName("should resolve resource name when valid pattern is provided")
        void shouldResolveResourceNameWhenValidPatternIsProvided(String resourceNamePattern) {
            // Given
            final String EXTENSION = "properties";
            final String PROFILE = "default";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {
                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(RESOURCE_NAME_PATTERN))
                        .thenReturn(true);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"/application/${profile}.properties", "../config../.json"})
        @DisplayName("should set empty resource name when pattern is invalid and default provider is used")
        void shouldSetEmptyResourceNameWhenPatternIsInvalidAndDefaultProvideIsUsed(String resourceNamePattern) {
            // Given
            final String STRING_EMPTY = "";
            when(configResourceLocation.getProvider()).thenReturn("classpath");
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(resourceNamePattern))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> STRING_EMPTY.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"/application/${profile}.properties", "../config../.json"})
        @DisplayName("should set empty resource name when pattern is invalid and non-default provider is used")
        void shouldSetEmptyResourceNameWhenPatternIsInvalidAndNonDefaultProvideIsUsed(String resourceNamePattern) {
            // Given
            final String STRING_EMPTY = "";
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(resourceNamePattern))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> STRING_EMPTY.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should set empty resource name for null or empty pattern with non-default provider")
        void shouldSetEmptyResourceNameForNullOrEmptyPatternWithNonDefaultProvider(String resourceNamePattern) {
            // Given
            final String STRING_EMPTY = "";
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {
                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(resourceNamePattern))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> STRING_EMPTY.equals(resource.getResourceName()))
                );
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should set empty resource name for blank pattern with non-default provider")
        void shouldSetEmptyResourceNameForBlankPatternWithNonDefaultProvider(String resourceNamePattern) {
            // Given
            final String STRING_EMPTY = "";
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceNamePattern()).thenReturn(resourceNamePattern);

            try (
                    MockedStatic<FilenameValidator> mockedIsSafeResourceName = mockStatic(FilenameValidator.class)
            ) {

                mockedIsSafeResourceName.when(() -> FilenameValidator.isSafeResourceName(resourceNamePattern))
                        .thenReturn(false);

                // When
                List<ResolvedConfigResource> result = preparer.prepare();

                // Then
                assertTrue(
                        result.stream().anyMatch(resource -> STRING_EMPTY.equals(resource.getResourceName()))
                );
            }
        }
    }

    @Nested
    @DisplayName("Variables Tests")
    class VariablesTests {
        @Test
        @DisplayName("should use default profile variable when provider is default and variables are null")
        void shouldAddDefaultProfileVariableWhenDefaultProviderAndVariablesNull() {
            // Given
            final String PATTERN = "application-${profile}.properties";
            final String EXTENSION = "properties";
            final String PROFILE = "default";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(PATTERN);
            when(configResourceLocation.getVariables()).thenReturn(null);

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
            );
        }

        @Test
        @DisplayName("should use default profile variable when provider is default and variables are empty")
        void shouldAddDefaultProfileVariableWhenDefaultProviderAndVariablesEmpty() {
            // Given
            final String PATTERN = "application-${profile}.properties";
            final String EXTENSION = "properties";
            final String PROFILE = "default";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(PATTERN);
            when(configResourceLocation.getVariables()).thenReturn(ConfigMap.emptyMap());

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
            );
        }

        @Test
        @DisplayName("should use provided variables when provider is not default")
        void shouldReturnProvidedVariablesWhenIsNotDefaultProvider() {
            // Given
            final String PATTERN = "application-${profile}.properties";
            final String EXTENSION = "properties";
            final String PROFILE = "dev";
            final String RESOURCE_NAME = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getResourceNamePattern()).thenReturn(PATTERN);
            when(configResourceLocation.getVariables()).thenReturn(ConfigMap.of("profile", PROFILE));

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> RESOURCE_NAME.equals(resource.getResourceName()))
            );
        }

        @Test
        @DisplayName("should return empty resource name when profile variable is missing")
        void shouldSkipResourceWhenVariablesMissingForInterpolation() {
            // Given
            final String PATTERN = "application-${profile}.properties";
            final String STRING_EMPTY = "";
            when(configResourceLocation.getResourceNamePattern()).thenReturn(PATTERN);
            when(configResourceLocation.getVariables()).thenReturn(ConfigMap.of("env", "dev"));

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> STRING_EMPTY.equals(resource.getResourceName()))
            );
        }

        @Test
        @DisplayName("should resolve resource name using profile variable when pattern is valid")
        void shouldResolveResourceNameAndExtensionWhenValid() {
            // Given
            final String PATTERN = "application-${profile}.properties";
            final String EXTENSION = "properties";
            final String PROFILE = "test";
            final String RESOURCE_NAME_PATTERN = "application-" + PROFILE + "." + EXTENSION;
            when(configResourceLocation.getResourceNamePattern()).thenReturn(PATTERN);
            when(configResourceLocation.getVariables()).thenReturn(ConfigMap.of("profile", PROFILE));

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().anyMatch(resource -> RESOURCE_NAME_PATTERN.equals(resource.getResourceName()))
            );
        }
    }

    @Nested
    @DisplayName("Credentials Tests")
    class CredentialsTests {

        @Test
        @DisplayName("should return empty credentials when provider is default and credentials are null")
        void shouldReturnEmptyCredentialsWhenDefaultProviderAndCredentialsNull() {
            // Given
            when(configResourceLocation.getCredentials()).thenReturn(null);

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().allMatch(resource -> resource.getCredentials().isEmpty())
            );
        }

        @Test
        @DisplayName("should return empty credentials when provider is default and credentials are empty")
        void shouldReturnEmptyCredentialsWhenDefaultProviderAndCredentialsEmpty() {
            // Given
            when(configResourceLocation.getCredentials()).thenReturn(ConfigMap.emptyMap());

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            assertTrue(
                    result.stream().allMatch(resource -> resource.getCredentials().isEmpty())
            );
        }

        @Test
        @DisplayName("should return provided credentials when provider is default and credentials are provided")
        void shouldReturnProvidedCredentialsWhenDefaultProviderAndCredentialsProvided() {
            // Given
            ConfigMap providedCredentials = ConfigMap.of("accessKey", "testKey");
            when(configResourceLocation.getCredentials()).thenReturn(providedCredentials);

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            result.forEach(resource ->
                    assertEquals(providedCredentials, resource.getCredentials())
            );
        }

        @Test
        @DisplayName("should return provided credentials when provider is not default")
        void shouldReturnProvidedCredentialsWhenProviderIsNotDefault() {
            // Given
            ConfigMap providedCredentials = ConfigMap.of("accessKey", "testKey");
            when(configResourceLocation.getProvider()).thenReturn("S3");
            when(configResourceLocation.getCredentials()).thenReturn(providedCredentials);

            // When
            List<ResolvedConfigResource> result = preparer.prepare();

            // Then
            result.forEach(resource ->
                    assertEquals(providedCredentials, resource.getCredentials())
            );
        }
    }
}
