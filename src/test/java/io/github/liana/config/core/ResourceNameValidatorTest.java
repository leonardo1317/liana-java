package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.liana.config.internal.ImmutableConfigSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceNameValidatorTest {

  private ResourceNameValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ResourceNameValidator(ImmutableConfigSet.of(Set.of("config")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should return false when resource name is blank")
  void shouldReturnFalseWhenResourceNameIsBlankOrNull(String resourceName) {
    assertFalse(validator.isSafeResourceName(resourceName));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("should return false when resource name is null")
  void shouldReturnFalseWhenResourceNameIsNull(String resourceName) {
    assertFalse(validator.isSafeResourceName(resourceName));
  }

  @Test
  @DisplayName("should return true for valid simple file names")
  void shouldReturnTrueForValidSimpleFileNames() {
    assertTrue(validator.isSafeResourceName("config.yaml"));
    assertTrue(validator.isSafeResourceName("settings.json"));
  }

  @Test
  @DisplayName("should return true for valid nested paths without traversal")
  void shouldReturnTrueForValidNestedPaths() {
    assertTrue(validator.isSafeResourceName("configs/env/prod.yaml"));
    assertTrue(validator.isSafeResourceName("data/resources/file.json"));
  }

  @Test
  @DisplayName("should return false when path contains directory traversal sequences")
  void shouldReturnFalseWhenContainsDirectoryTraversal() {
    assertTrue(validator.isSafeResourceName("etc/passwd"));
    assertFalse(validator.isSafeResourceName("../etc/passwd"));
    assertFalse(validator.isSafeResourceName("configs/../../secrets.yaml"));
    assertFalse(validator.isSafeResourceName("../../hidden/config.yaml"));
  }

  @Test
  @DisplayName("should return false when normalized path starts with '..'")
  void shouldReturnFalseWhenNormalizedPathStartsWithDotDot() {
    assertFalse(validator.isSafeResourceName("..\\config.yaml")); // Windows-style path
  }

  @Test
  @DisplayName("should handle mixed path separators safely")
  void shouldHandleMixedPathSeparatorsSafely() {
    assertTrue(validator.isSafeResourceName("folder\\subdir/config.yaml"));
    assertFalse(validator.isSafeResourceName("..\\..\\secrets\\config.yaml"));
  }

  @Test
  @DisplayName("should normalize redundant path segments correctly")
  void shouldNormalizeRedundantPathSegmentsCorrectly() {
    assertTrue(validator.isSafeResourceName("config/settings.yaml"));
    assertTrue(validator.isSafeResourceName("config/../app/secrets.yaml"));
    assertFalse(validator.isSafeResourceName("docs/../../../../etc/passwd"));
  }

  @Test
  @DisplayName("should treat uppercase extensions as valid since normalization ignores case")
  void shouldTreatUppercaseExtensionsAsValid() {
    assertTrue(validator.isSafeResourceName("CONFIG.JSON"));
    assertTrue(validator.isSafeResourceName("data/SETTINGS.YAML"));
  }

  @Test
  @DisplayName("should return true even if file has no extension but is safe")
  void shouldReturnTrueIfNoExtensionButSafePath() {
    assertTrue(validator.isSafeResourceName("config/defaults"));
  }

  @Test
  @DisplayName("should return false if path contains traversal encoded in different form")
  void shouldReturnFalseIfTraversalEncodedDifferently() {
    assertTrue(validator.isSafeResourceName("%2e%2e/config.yaml"));
  }

  @Test
  @DisplayName("should return false when resource name causes InvalidPathException internally")
  void shouldReturnFalseWhenInvalidPathExceptionOccurs() {
    String invalidResourceName = "invalid\0name.yaml";

    boolean result = validator.isSafeResourceName(invalidResourceName);

    assertFalse(result);
  }
}
