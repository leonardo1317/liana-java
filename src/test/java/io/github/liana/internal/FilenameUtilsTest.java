package io.github.liana.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilenameUtilsTest {

  @Test
  @DisplayName("should return file name from valid path")
  void shouldReturnFileNameFromValidPath() {
    String result = FilenameUtils.getName("config/resource/config.yaml");
    assertEquals("config.yaml", result);
  }

  @Test
  @DisplayName("should return same name when path has no directories")
  void shouldReturnSameNameWhenNoDirectories() {
    String result = FilenameUtils.getName("config.yaml");
    assertEquals("config.yaml", result);
  }

  @Test
  @DisplayName("should throw NullPointerException when path is null in getName")
  void shouldThrowNullPointerExceptionWhenPathIsNullInGetName() {
    assertThrows(NullPointerException.class, () -> FilenameUtils.getName(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when path is invalid in getName")
  void shouldThrowIllegalArgumentExceptionWhenPathIsInvalidInGetName() {
    assertThrows(IllegalArgumentException.class, () -> FilenameUtils.getName("config\0.yaml"));
  }

  @Test
  @DisplayName("should return extension for file with single dot")
  void shouldReturnExtensionForSingleDot() {
    String result = FilenameUtils.getExtension("config.yaml");
    assertEquals("yaml", result);
  }

  @Test
  @DisplayName("should return extension for multi-dot filename")
  void shouldReturnExtensionForMultiDotFile() {
    String result = FilenameUtils.getExtension("config.dev.yaml");
    assertEquals("yaml", result);
  }

  @Test
  @DisplayName("should return empty string when file has no extension")
  void shouldReturnEmptyWhenNoExtension() {
    String result = FilenameUtils.getExtension("config");
    assertEquals("", result);
  }

  @Test
  @DisplayName("should return empty string when path is blank")
  void shouldReturnEmptyWhenBlankPath() {
    String result = FilenameUtils.getExtension(" ");
    assertEquals("", result);
  }

  @Test
  @DisplayName("should throw NullPointerException when path is null in getExtension")
  void shouldThrowNullPointerExceptionWhenPathIsNullInGetExtension() {
    assertThrows(NullPointerException.class, () -> FilenameUtils.getExtension(null));
  }
}
