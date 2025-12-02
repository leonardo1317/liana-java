package io.github.liana.config.api;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.github.liana.config.core.DefaultResourceLocationBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceLocationTest {

  @Test
  @DisplayName("should return a non-null builder instance")
  void shouldReturnNonNullBuilderInstance() {
    ResourceLocationBuilder builder = ResourceLocation.builder();

    assertNotNull(builder);
    assertInstanceOf(DefaultResourceLocationBuilder.class, builder);
  }

  @Test
  @DisplayName("should always return a new builder instance on each call")
  void shouldReturnNewBuilderInstanceEachTime() {
    ResourceLocationBuilder firstBuilder = ResourceLocation.builder();
    ResourceLocationBuilder secondBuilder = ResourceLocation.builder();

    assertNotSame(firstBuilder, secondBuilder);
  }

}