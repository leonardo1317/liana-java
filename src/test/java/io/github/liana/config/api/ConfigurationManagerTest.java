package io.github.liana.config.api;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigurationManagerTest {

  @Test
  @DisplayName("should return a non-null builder instance")
  void shouldReturnNonNullBuilderInstance() {
    ConfigurationManagerBuilder builder = ConfigurationManager.builder();

    assertNotNull(builder);
    assertInstanceOf(ConfigurationManagerBuilder.class, builder);
  }

  @Test
  @DisplayName("should always return a new builder instance on each call")
  void shouldReturnNewBuilderInstanceEachTime() {
    ConfigurationManagerBuilder firstBuilder = ConfigurationManager.builder();
    ConfigurationManagerBuilder secondBuilder = ConfigurationManager.builder();

    assertNotSame(firstBuilder, secondBuilder);
  }
}