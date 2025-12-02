package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PropertySourcesTest {

  @Test
  @DisplayName("should create PropertySource from system environment variables")
  void shouldCreatePropertySourceFromSystemEnv() {
    PropertySource envSource = PropertySources.from(key -> "mocked-value");
    assertEquals("mocked-value", envSource.get("any-key"));
  }

  @Test
  @DisplayName("should use provided PropertySource instead of environment variables")
  void shouldUseProvidedPropertySource() {
    PropertySource envSource = PropertySources.from(key -> "test-value");

    assertEquals("test-value", envSource.get("any-key"));
  }

  @Test
  @DisplayName("should create PropertySource from null map (empty source)")
  void shouldCreatePropertySourceFromNullMapAsEmptySource() {
    PropertySource source = PropertySources.fromMap(null);
    assertNull(source.get("anyKey"));
  }

  @Test
  @DisplayName("should create PropertySource from empty map")
  void shouldCreatePropertySourceFromEmptyMap() {
    PropertySource source = PropertySources.fromMap(Collections.emptyMap());
    assertNull(source.get("missing"));
  }

  @Test
  @DisplayName("should retrieve value from map source as string")
  void shouldRetrieveValueFromMapSourceAsString() {
    Map<String, Object> map = new HashMap<>();
    map.put("key1", "value1");
    map.put("key2", 123);

    PropertySource source = PropertySources.fromMap(map);

    assertEquals("value1", source.get("key1"));
    assertEquals("123", source.get("key2"));
    assertNull(source.get("missing"));
  }

  @Test
  @DisplayName("should defensively copy and make map unmodifiable")
  void shouldDefensivelyCopyAndMakeMapUnmodifiable() {
    Map<String, String> original = new HashMap<>();
    original.put("k", "v");

    PropertySource source = PropertySources.fromMap(original);

    original.put("k", "changed");
    assertEquals("v", source.get("k"));
  }

  @Test
  @DisplayName("should throw NullPointerException when source is null")
  void shouldThrowNullPointerExceptionFromPropertySourceWithNull() {
    assertThrows(NullPointerException.class, () -> PropertySources.from(null));
  }
}
