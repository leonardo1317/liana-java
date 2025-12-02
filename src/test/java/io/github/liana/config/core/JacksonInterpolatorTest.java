package io.github.liana.config.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.internal.ImmutableConfigMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JacksonInterpolatorTest {

  private JacksonInterpolator interpolator;

  @Mock
  private Placeholder placeholder;

  @BeforeEach
  void setUp() {
    interpolator = new JacksonInterpolator(new ObjectMapper());
  }

  @Test
  @DisplayName("should return unmodifiable empty map when source is empty")
  void shouldReturnUnmodifiableEmptyMapWhenSourceIsEmpty() {
    Map<String, Object> source = Collections.emptyMap();
    var vars = ImmutableConfigMap.of(Map.of("VAR", "value"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder, vars);

    assertTrue(result.isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> result.put("key", "value"));
  }

  @Test
  @DisplayName("should return unmodifiable map when variables are empty")
  void shouldReturnUnmodifiableMapWhenVariablesAreEmpty() {
    Map<String, Object> source = Map.of("key", "value");
    var vars = ImmutableConfigMap.empty();

    Map<String, Object> result = interpolator.interpolate(source, placeholder, vars);

    assertEquals(source, result);
    assertThrows(UnsupportedOperationException.class, () -> result.put("key2", "value2"));
  }

  @Test
  @DisplayName("should interpolate simple map values using placeholder")
  void shouldInterpolateSimpleMapValues() {
    Map<String, Object> source = Map.of(
        "user", "${USER}",
        "password", "${PASS}"
    );

    Map<String, String> vars = Map.of(
        "USER", "Alice",
        "PASS", "secret"
    );

    when(placeholder.replaceIfAllResolvable("${USER}", vars)).thenReturn(Optional.of("Alice"));
    when(placeholder.replaceIfAllResolvable("${PASS}", vars)).thenReturn(Optional.of("secret"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder,
        ImmutableConfigMap.of(vars));

    assertEquals("Alice", result.get("user"));
    assertEquals("secret", result.get("password"));
  }

  @Test
  @DisplayName("should handle unresolved placeholders without change")
  void shouldHandleUnresolvedPlaceholdersWithoutChange() {
    Map<String, Object> source = Map.of("key", "${UNKNOWN}");
    Map<String, String> vars = Map.of("SOME_VAR", "value");

    when(placeholder.replaceIfAllResolvable("${UNKNOWN}", vars)).thenReturn(Optional.empty());

    Map<String, Object> result = interpolator.interpolate(source, placeholder,
        ImmutableConfigMap.of(vars));

    assertEquals("${UNKNOWN}", result.get("key"));
  }

  @Test
  @DisplayName("should interpolate nested maps")
  void shouldInterpolateNestedMaps() {
    Map<String, Object> source = Map.of(
        "level1", Map.of(
            "level2", "${VAR}"
        )
    );

    Map<String, String> vars = Map.of("VAR", "value");

    when(placeholder.replaceIfAllResolvable("${VAR}", vars)).thenReturn(Optional.of("value"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder,
        ImmutableConfigMap.of(vars));

    Map<String, Object> level1 = (Map<String, Object>) result.get("level1");
    assertEquals("value", level1.get("level2"));
  }

  @Test
  @DisplayName("should interpolate arrays in map")
  void shouldInterpolateArraysInMap() {
    Map<String, Object> source = Map.of(
        "list", List.of("${A}", "${B}", 123)
    );
    Map<String, String> vars = Map.of("A", "one", "B", "two");

    when(placeholder.replaceIfAllResolvable("${A}", vars)).thenReturn(Optional.of("one"));
    when(placeholder.replaceIfAllResolvable("${B}", vars)).thenReturn(Optional.of("two"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder,
        ImmutableConfigMap.of(vars));

    List<Object> list = (List<Object>) result.get("list");
    assertEquals(List.of("one", "two", 123), list);
  }

  @Test
  @DisplayName("should interpolate arrays of maps")
  void shouldInterpolateArraysOfMaps() {
    Map<String, Object> source = Map.of(
        "users", List.of(
            Map.of("name", "${USER1}"),
            Map.of("name", "${USER2}")
        )
    );

    Map<String, String> vars = Map.of("USER1", "Alice", "USER2", "Bob");

    when(placeholder.replaceIfAllResolvable("${USER1}", vars)).thenReturn(Optional.of("Alice"));
    when(placeholder.replaceIfAllResolvable("${USER2}", vars)).thenReturn(Optional.of("Bob"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder,
        ImmutableConfigMap.of(vars));
    List<Map<String, Object>> users = (List<Map<String, Object>>) result.get("users");

    assertEquals("Alice", users.get(0).get("name"));
    assertEquals("Bob", users.get(1).get("name"));
  }

  @Test
  @DisplayName("should throw NullPointerException for null arguments")
  void shouldThrowNullPointerExceptionForNullArguments() {
    Map<String, Object> source = Map.of();
    var vars = ImmutableConfigMap.empty();

    assertThrows(NullPointerException.class,
        () -> interpolator.interpolate(null, placeholder, vars));
    assertThrows(NullPointerException.class,
        () -> interpolator.interpolate(source, null, vars));
    assertThrows(NullPointerException.class,
        () -> interpolator.interpolate(source, placeholder, null));
  }

  @Test
  @DisplayName("should not modify the original map")
  void shouldNotModifyTheOriginalMap() {
    Map<String, Object> source = new HashMap<>();
    source.put("key", "${VAR}");
    Map<String, String> vars = Map.of("VAR", "value");

    when(placeholder.replaceIfAllResolvable("${VAR}", vars)).thenReturn(Optional.of("value"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder,
        ImmutableConfigMap.of(vars));

    assertEquals("${VAR}", source.get("key"));
    assertEquals("value", result.get("key"));
  }

  @Test
  @DisplayName("should handle null node returned by convert")
  void shouldHandleNullNodeFromConvert() {
    JacksonInterpolator interpolator = spy(new JacksonInterpolator(new ObjectMapper()));

    doReturn(null).when(interpolator).executeWithResult(any(), anyString());

    Map<String, Object> source = Map.of("key", "value");
    var vars = ImmutableConfigMap.of(Map.of("VAR", "value"));

    assertThrows(IllegalStateException.class,
        () -> interpolator.interpolate(source, placeholder, vars));
  }

  @Test
  @DisplayName("should skip empty string values")
  void shouldSkipEmptyStringValues() {
    Map<String, Object> source = Map.of("key", "");
    var vars = ImmutableConfigMap.of(Map.of("VAR", "value"));

    Map<String, Object> result = interpolator.interpolate(source, placeholder, vars);

    assertEquals("", result.get("key"));
  }
}
