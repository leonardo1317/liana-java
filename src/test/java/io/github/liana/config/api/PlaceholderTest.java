package io.github.liana.config.api;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceholderTest {

  @Test
  @DisplayName("should return a non-null builder instance")
  void shouldReturnNonNullBuilderInstance() {
    PlaceholderBuilder builder = Placeholder.builder();

    assertNotNull(builder);
    assertInstanceOf(PlaceholderBuilder.class, builder);
  }

  @Test
  @DisplayName("should return a new instance on each builder() call")
  void shouldReturnNewInstanceOnEachBuilderCall() {
    PlaceholderBuilder first = Placeholder.builder();
    PlaceholderBuilder second = Placeholder.builder();
    assertNotSame(first, second);
  }
}
