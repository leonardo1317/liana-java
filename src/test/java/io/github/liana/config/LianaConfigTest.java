package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LianaConfigTest {

  @Test
  @DisplayName("should return the same ConfigManager instance (singleton behavior)")
  void shouldReturnSameInstance() {
    ConfigManager instance1 = LianaConfig.getInstance();
    ConfigManager instance2 = LianaConfig.getInstance();

    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }

  @Test
  @DisplayName("should return a new builder instance each time")
  void shouldReturnNewBuilderInstance() {
    LianaConfigBuilder builder1 = LianaConfig.builder();
    LianaConfigBuilder builder2 = LianaConfig.builder();

    assertNotNull(builder1);
    assertNotNull(builder2);
    assertNotSame(builder1, builder2);
  }

  @Test
  @DisplayName("should build a new ConfigManager different from the singleton")
  void shouldBuildNewCustomConfigManager() {
    ConfigManager singleton = LianaConfig.getInstance();
    ConfigManager custom = LianaConfig.builder().build();

    assertNotNull(custom);
    assertNotSame(singleton, custom);
  }
}
