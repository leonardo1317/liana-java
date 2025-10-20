package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigResourceLocationsTest {

  @Test
  @DisplayName("should return a non-null builder instance")
  void shouldReturnNonNullBuilderInstance() {
    ConfigResourceLocationBuilder builder = ConfigResourceLocations.builder();

    assertNotNull(builder);
    assertInstanceOf(DefaultConfigResourceLocationBuilder.class, builder);
  }

  @Test
  @DisplayName("should always return a new builder instance on each call")
  void shouldReturnNewBuilderInstanceEachTime() {
    ConfigResourceLocationBuilder firstBuilder = ConfigResourceLocations.builder();
    ConfigResourceLocationBuilder secondBuilder = ConfigResourceLocations.builder();

    assertNotSame(firstBuilder, secondBuilder);
  }
}
