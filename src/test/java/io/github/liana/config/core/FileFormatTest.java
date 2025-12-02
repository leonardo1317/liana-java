package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileFormatTest {

  @Test
  @DisplayName("should return expected extensions for PROPERTIES format")
  void shouldReturnExpectedExtensionsForProperties() {
    Set<String> extensions = FileFormat.PROPERTIES.getExtensions();
    assertIterableEquals(List.of("properties"), extensions);
  }

  @Test
  @DisplayName("should return expected extensions for YAML format")
  void shouldReturnExpectedExtensionsForYaml() {
    Set<String> extensions = FileFormat.YAML.getExtensions();
    assertIterableEquals(List.of("yaml", "yml"), extensions);
  }

  @Test
  @DisplayName("should return expected extensions for JSON format")
  void shouldReturnExpectedExtensionsForJson() {
    Set<String> extensions = FileFormat.JSON.getExtensions();
    assertIterableEquals(List.of("json"), extensions);
  }

  @Test
  @DisplayName("should return expected extensions for XML format")
  void shouldReturnExpectedExtensionsForXml() {
    Set<String> extensions = FileFormat.XML.getExtensions();
    assertIterableEquals(List.of("xml"), extensions);
  }

  @Test
  @DisplayName("should have unique extensions")
  void shouldHaveUniqueExtensions() {
    Set<String> seen = new HashSet<>();
    for (FileFormat format : FileFormat.values()) {
      for (String extension : format.getExtensions()) {
        assertTrue(seen.add(extension),
            "extension '" + extension + "' is used in multiple formats!");
      }
    }
  }
}
