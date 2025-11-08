package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import io.github.liana.config.exception.ConversionException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JacksonValueResolverTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @DisplayName("should get entire source as typed map")
  void shouldGetEntireSourceAsTypedMap() {
    Map<String, Object> source = Map.of("env", "dev");
    var resolver = new JacksonValueResolver(objectMapper, source);
    Type type = new TypeReference<Map<String, Object>>() {
    }.getType();
    Optional<Map<String, Object>> result = resolver.getRootAs(type);
    assertTrue(result.isPresent());
    assertEquals("dev", result.get().get("env"));
  }

  @Test
  @DisplayName("should return empty optional when rootNode is MissingNode")
  void shouldReturnEmptyOptionalWhenRootNodeIsMissing() {
    Type type = new TypeReference<Map<String, Object>>() {
    }.getType();

    Map<String, Object> source = Map.of("name", "my-service");
    var spyMapper = spy(new ObjectMapper());

    doReturn(MissingNode.getInstance()).when(spyMapper).valueToTree(source);

    var resolver = new JacksonValueResolver(spyMapper, source);

    Optional<Map<String, Object>> result = resolver.getRootAs(type);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should return empty optional when source missing")
  void shouldReturnEmptyOptionalWhenSourceMissing() {
    Type type = new TypeReference<Map<String, Object>>() {
    }.getType();
    Map<String, Object> source = Map.of("name", "my-service");
    var resolver = new JacksonValueResolver(objectMapper, source);

    Optional<Map<String, Object>> result = resolver.get("port", type);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("should get nested value using path and type")
  void shouldGetNestedValueUsingPathAndType() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    Optional<String> name = resolver.get("app.name", String.class);
    assertTrue(name.isPresent());
    assertEquals("my-service", name.get());
  }

  @Test
  @DisplayName("should return empty optional when path missing")
  void shouldReturnEmptyOptionalWhenPathMissing() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );
    var resolver = new JacksonValueResolver(objectMapper, source);

    Optional<String> missing = resolver.get("app.unknown", String.class);
    assertTrue(missing.isEmpty());
  }

  @Test
  @DisplayName("should get list of values from array path")
  void shouldGetListOfValuesFromArrayPath() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service",
            "features", List.of("logging", "metrics")
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    List<String> features = resolver.getList("app.features", String.class);
    assertEquals(List.of("logging", "metrics"), features);
  }

  @Test
  @DisplayName("should return empty list when path not found or not array")
  void shouldReturnEmptyListWhenPathNotFoundOrNotArray() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    assertTrue(resolver.getList("app.unknown", String.class).isEmpty());
    assertTrue(resolver.getList("app.name", String.class).isEmpty());
  }

  @Test
  @DisplayName("should get map from object path")
  void shouldGetMapFromObjectPath() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service",
            "port", 8080
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    Map<String, Object> appMap = resolver.getMap("app", Object.class);
    assertEquals("my-service", appMap.get("name"));
    assertEquals(8080, appMap.get("port"));
  }

  @Test
  @DisplayName("should return empty map when path not found or not object")
  void shouldReturnEmptyMapWhenPathNotFoundOrNotObject() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    assertTrue(resolver.getMap("app.unknown", Object.class).isEmpty());
    assertTrue(resolver.getMap("app.features", Object.class).isEmpty());
  }

  @Test
  @DisplayName("should return original source map from getRoot")
  void shouldReturnOriginalSourceMapFromGetRoot() {
    Map<String, Object> source = Map.of(
        "app", Map.of("name", "my-service")
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    Map<String, Object> root = resolver.getRootAsMap();

    assertEquals(source, root);
    assertTrue(root.containsKey("app"));
    assertEquals("my-service", ((Map<?, ?>) root.get("app")).get("name"));
  }

  @Test
  @DisplayName("should not allow external modification of source map after resolver creation")
  void shouldNotAllowExternalModificationOfSourceMapAfterResolverCreation() {
    Map<String, Object> mutableSource = new HashMap<>();
    mutableSource.put("key", "value");

    var resolver = new JacksonValueResolver(objectMapper, mutableSource);

    mutableSource.put("key2", "new-value");

    Map<String, Object> root = resolver.getRootAsMap();

    assertFalse(root.containsKey("key2"));
    assertEquals("value", root.get("key"));
  }

  @Test
  @DisplayName("should throw UnsupportedOperationException when attempting to modify getRoot result")
  void shouldThrowExceptionWhenModifyingGetRootResult() {
    Map<String, Object> source = Map.of("env", "dev");
    var resolver = new JacksonValueResolver(objectMapper, source);

    Map<String, Object> root = resolver.getRootAsMap();

    assertThrows(UnsupportedOperationException.class, () -> root.put("newKey", "value"));
    assertThrows(UnsupportedOperationException.class, () -> root.remove("env"));
    assertThrows(UnsupportedOperationException.class, root::clear);
  }

  @Test
  @DisplayName("should check if path exists")
  void shouldCheckIfPathExists() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "name", "my-service"
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    assertTrue(resolver.containsKey("app.name"));
    assertFalse(resolver.containsKey("app.unknown"));
  }

  @Test
  @DisplayName("should throw ConversionException for invalid conversion")
  void shouldThrowConversionExceptionForInvalidConversion() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "port", 8080
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    assertThrows(ConversionException.class,
        () -> resolver.get("app.port", UUID.class).orElseThrow());
  }

  @Test
  @DisplayName("should return element when using array index with get")
  void shouldReturnElementWhenUsingArrayIndexWithGet() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    Optional<String> element = resolver.get("app.features[0]", String.class);
    assertTrue(element.isPresent());
    assertEquals("logging", element.get());
  }

  @Test
  @DisplayName("should return full list when path points to array")
  void shouldReturnFullListWhenPathPointsToArray() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    List<String> features = resolver.getList("app.features", String.class);
    assertEquals(List.of("logging", "metrics"), features);
  }

  @Test
  @DisplayName("should return empty list when using array index with get list")
  void shouldReturnEmptyListWhenUsingArrayIndexWithGetList() {
    Map<String, Object> source = Map.of(
        "app", Map.of(
            "features", List.of("logging", "metrics")
        )
    );

    var resolver = new JacksonValueResolver(objectMapper, source);

    List<String> features = resolver.getList("app.features[0]", String.class);
    assertTrue(features.isEmpty());
  }


  @Nested
  @DisplayName("Null handling")
  class NullHandlingTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("should throw NullPointerException when sources is null")
    void shouldThrowWhenSourcesIsNull() {
      Map<String, Object> source = null;
      assertThrows(NullPointerException.class, () -> new JacksonValueResolver(objectMapper, source));
    }

    @Nested
    @DisplayName("when path is null")
    class PathNullTests {

      private final Map<String, Object> source = Map.of("app", Map.of("name", "my-service"));
      private final JacksonValueResolver resolver = new JacksonValueResolver(objectMapper, source);

      @Test
      @DisplayName("should throw NullPointerException when getting")
      void shouldThrowWhenGetting() {
        assertThrows(NullPointerException.class, () -> resolver.get(null, String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting list")
      void shouldThrowWhenGettingList() {
        assertThrows(NullPointerException.class, () -> resolver.getList(null, String.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting map")
      void shouldThrowWhenGettingMap() {
        assertThrows(NullPointerException.class, () -> resolver.getMap(null, Object.class));
      }

      @Test
      @DisplayName("should throw NullPointerException when checking path existence")
      void shouldThrowWhenCheckingPath() {
        assertThrows(NullPointerException.class, () -> resolver.containsKey(null));
      }
    }

    @Nested
    @DisplayName("when targetType is null")
    class TargetTypeNullTests {

      private final Map<String, Object> source = Map.of(
          "app", Map.of("name", "my-service", "features", List.of("logging", "metrics"))
      );
      private final JacksonValueResolver resolver = new JacksonValueResolver(objectMapper, source);

      @Test
      @DisplayName("should throw NullPointerException when getting by type")
      void shouldThrowWhenGettingByType() {
        assertThrows(NullPointerException.class, () -> resolver.getRootAs(null));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting by path")
      void shouldThrowWhenGettingByPath() {
        assertThrows(NullPointerException.class, () -> resolver.get("app.name", null));
      }

      @Test
      @DisplayName("should throw NullPointerException when getting list")
      void shouldThrowWhenGettingList() {
        assertThrows(NullPointerException.class,
            () -> resolver.getList("app.features", null));
      }
    }
  }
}
