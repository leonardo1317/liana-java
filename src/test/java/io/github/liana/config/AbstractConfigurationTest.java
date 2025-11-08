package io.github.liana.config;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConversionException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractConfigurationTest {

  @Mock
  private ValueResolver resolver;

  private Configuration configuration;

  @BeforeEach
  void setUp() {
    configuration = new AbstractConfiguration(resolver) {
    };
  }

  @Nested
  @DisplayName("Constructor")
  final class ConstructorTests {

    @Test
    @DisplayName("should throw NullPointerException when resolver is null")
    void shouldThrowExceptionWhenResolverIsNull() {
      assertThrows(NullPointerException.class, () -> new AbstractConfiguration(null) {
      });
    }
  }

  @Nested
  @DisplayName("containsKey(key) method")
  final class ContainsKeyMethodTests {

    @Test
    @DisplayName("should return true when resolver reports key exists")
    void shouldReturnTrueWhenKeyExists() {
      when(resolver.containsKey(anyString())).thenReturn(true);

      assertTrue(configuration.containsKey("app.name"));
      verify(resolver).containsKey(anyString());
    }

    @Test
    @DisplayName("should return false when resolver reports key does not exist")
    void shouldReturnFalseWhenKeyDoesNotExist() {
      when(resolver.containsKey(anyString())).thenReturn(false);

      assertFalse(configuration.containsKey("missing.key"));
      verify(resolver).containsKey(anyString());
    }

    @Test
    @DisplayName("should throw NullPointerException when key is null")
    void shouldThrowNullPointerExceptionWhenKeyIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.containsKey(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should throw IllegalArgumentException when key is blank")
    void shouldThrowWhenKeyIsBlank(String key) {
      assertThrows(IllegalArgumentException.class, () -> configuration.containsKey(key));
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      when(resolver.containsKey(anyString())).thenThrow(new ConversionException("invalid key"));
      assertThrows(ConversionException.class, () -> configuration.containsKey("bad.key"));
    }
  }

  @Nested
  @DisplayName("get(key, Class) method")
  final class GetMethodTests {

    @Test
    @DisplayName("should delegate get call to resolver and return Optional<String>")
    void shouldDelegateGetToResolver() {
      var type = String.class;
      when(resolver.get(anyString(), eq(type))).thenReturn(Optional.of("service"));

      Optional<String> result = configuration.get("app.name", type);

      assertEquals(Optional.of("service"), result);
      verify(resolver).get(anyString(), eq(type));
    }

    @Test
    @DisplayName("should support simple POJO record mapping")
    void shouldSupportSimplePojoMapping() {
      record AppConfig(String url, int timeout) {

      }
      var type = AppConfig.class;
      AppConfig expected = new AppConfig("http://localhost", 8080);

      doReturn(Optional.of(expected)).when(resolver).get(anyString(), eq(type));

      Optional<AppConfig> result = configuration.get("config", type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).get(anyString(), eq(type));
    }

    @Test
    @DisplayName("should throw NullPointerException when key is null")
    void shouldThrowNullPointerExceptionWhenKeyIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.get(null, String.class));
    }

    @Test
    @DisplayName("should throw NullPointerException when type is null")
    void shouldThrowNullPointerExceptionWhenTypeIsNull() {
      Class<String> value = null;
      assertThrows(NullPointerException.class, () -> configuration.get("key", value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should throw IllegalArgumentException when key is blank")
    void shouldThrowWhenKeyIsBlank(String key) {
      assertThrows(IllegalArgumentException.class, () -> configuration.get(key, String.class));
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      when(resolver.get(anyString(), any())).thenThrow(new ConversionException("bad type"));
      assertThrows(ConversionException.class, () -> configuration.get("invalid", String.class));
    }
  }


  @Nested
  @DisplayName("get(key, Type) method")
  final class GetTypeMethodTests {

    @Test
    @DisplayName("should support List<AppConfig>")
    void shouldSupportListOfPojo() {
      record AppConfig(String url, int timeout) {

      }

      var type = new TypeOf<List<AppConfig>>() {
      };
      List<AppConfig> expected = List.of(
          new AppConfig("http://localhost", 1000),
          new AppConfig("http://localhost", 2000)
      );

      doReturn(Optional.of(expected)).when(resolver).get(anyString(), eq(type.getType()));

      Optional<List<AppConfig>> result = configuration.get("configs", type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).get(anyString(), eq(type.getType()));
    }

    @Test
    @DisplayName("should support Map<String, AppConfig>")
    void shouldSupportMapOfPojo() {
      record AppConfig(String url, int timeout) {

      }

      var type = new TypeOf<Map<String, AppConfig>>() {
      };
      Map<String, AppConfig> expected = Map.of(
          "serviceA", new AppConfig("http://localhost", 1000),
          "serviceB", new AppConfig("http://localhost", 2000)
      );

      doReturn(Optional.of(expected)).when(resolver).get(anyString(), eq(type.getType()));

      Optional<Map<String, AppConfig>> result = configuration.get("services", type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).get(anyString(), eq(type.getType()));
    }

    @Test
    @DisplayName("should handle nested Map<String, List<AppConfig>>")
    void shouldHandleNestedMapOfListOfPojo() {
      record AppConfig(String url, int timeout) {

      }

      var type = new TypeOf<Map<String, List<AppConfig>>>() {
      };
      Map<String, List<AppConfig>> expected = Map.of(
          "clients",
          List.of(new AppConfig("http://localhost", 1000),
              new AppConfig("http://localhost", 2000)
          )
      );

      doReturn(Optional.of(expected)).when(resolver).get(anyString(), eq(type.getType()));

      Optional<Map<String, List<AppConfig>>> result = configuration.get("clients", type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).get(anyString(), eq(type.getType()));
    }

    @Test
    @DisplayName("should handle List<Map<String, AppConfig>>")
    void shouldHandleListOfMapsWithPojo() {
      record AppConfig(String url, int timeout) {

      }

      var type = new TypeOf<List<Map<String, AppConfig>>>() {
      };
      List<Map<String, AppConfig>> expected = List.of(
          Map.of("serviceA", new AppConfig("http://localhost", 1000)),
          Map.of("serviceB", new AppConfig("http://localhost", 2000))
      );

      doReturn(Optional.of(expected)).when(resolver).get(anyString(), eq(type.getType()));

      Optional<List<Map<String, AppConfig>>> result = configuration.get("serviceGroups", type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).get(anyString(), eq(type.getType()));
    }

    @Test
    @DisplayName("should handle nested Map<String, Map<String, AppConfig>>")
    void shouldHandleNestedMapsWithPojo() {
      record AppConfig(String url, int timeout) {

      }

      var type = new TypeOf<Map<String, Map<String, AppConfig>>>() {
      };
      Map<String, Map<String, AppConfig>> expected = Map.of(
          "region1", Map.of("clientA", new AppConfig("http://localhost", 1000)),
          "region2", Map.of("clientB", new AppConfig("http://localhost", 2000))
      );

      doReturn(Optional.of(expected)).when(resolver).get(anyString(), eq(type.getType()));

      Optional<Map<String, Map<String, AppConfig>>> result = configuration.get("regions", type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).get(anyString(), eq(type.getType()));
    }

    @Test
    @DisplayName("should throw NullPointerException when key is null")
    void shouldThrowNullPointerExceptionWhenKeyIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.get(null, new TypeOf<String>() {
      }));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should throw IllegalArgumentException when key is blank")
    void shouldThrowWhenKeyIsBlank(String key) {
      assertThrows(IllegalArgumentException.class,
          () -> configuration.get(key, new TypeOf<String>() {
          }));
    }

    @Test
    @DisplayName("should throw NullPointerException when type is null")
    void shouldThrowNullPointerExceptionWhenTypeIsNull() {
      TypeOf<String> value = null;
      assertThrows(NullPointerException.class, () -> configuration.get("key", value));
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      when(resolver.get(anyString(), any())).thenThrow(new ConversionException("bad type"));
      assertThrows(ConversionException.class,
          () -> configuration.get("invalid", new TypeOf<String>() {
          }));
    }
  }

  @Nested
  @DisplayName("getList(key, Class) method")
  final class GetListMethodTests {

    @Test
    @DisplayName("should return empty list when resolver returns empty list")
    void shouldReturnEmptyListWhenResolverReturnsEmptyList() {
      var type = String.class;
      when(resolver.getList(anyString(), eq(type))).thenReturn(emptyList());

      List<String> result = configuration.getList("env", type);

      assertTrue(result.isEmpty());
      assertSame(emptyList(), result);
    }

    @Test
    @DisplayName("should return unmodifiable copy of list when resolver returns non-empty list")
    void shouldReturnUnmodifiableListWhenResolverReturnsNonEmptyList() {
      var type = String.class;
      List<String> original = List.of("stage", "test");
      when(resolver.getList(anyString(), eq(type))).thenReturn(original);

      List<String> result = configuration.getList("env", type);

      assertEquals(original, result);
      assertThrows(UnsupportedOperationException.class, () -> result.add("beta"));
    }

    @Test
    @DisplayName("should throw NullPointerException when key is null")
    void shouldThrowNullPointerWhenKeyIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.getList(null, String.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should throw IllegalArgumentException when key is blank")
    void shouldThrowWhenKeyIsBlank(String key) {
      assertThrows(IllegalArgumentException.class, () -> configuration.getList(key, String.class));
    }

    @Test
    @DisplayName("should throw NullPointerException when class is null")
    void shouldThrowNullPointerWhenClassIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.getList("env", null));
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      when(resolver.getList(anyString(), eq(String.class))).thenThrow(
          new ConversionException("invalid"));
      assertThrows(ConversionException.class, () -> configuration.getList("env", String.class));
    }
  }

  @Nested
  @DisplayName("getMap(key, Class) method")
  final class GetMapMethodTests {

    @Test
    @DisplayName("should return empty map when resolver returns empty map")
    void shouldReturnEmptyMapWhenResolverReturnsEmptyMap() {
      var type = String.class;
      when(resolver.getMap(anyString(), eq(type))).thenReturn(emptyMap());

      Map<String, String> result = configuration.getMap("env", type);

      assertTrue(result.isEmpty());
      assertSame(emptyMap(), result);
    }

    @Test
    @DisplayName("should return unmodifiable copy of map when resolver returns non-empty map")
    void shouldReturnUnmodifiableMapWhenResolverReturnsNonEmptyMap() {
      var type = String.class;
      Map<String, String> original = Map.of("clientA", "http://localhost");
      when(resolver.getMap(anyString(), eq(type))).thenReturn(original);

      Map<String, String> result = configuration.getMap("env", type);

      assertEquals(original, result);
      assertNotSame(original, result);
      assertThrows(UnsupportedOperationException.class,
          () -> result.put("clientB", "http://localhost"));
    }

    @Test
    @DisplayName("should throw NullPointerException when key is null")
    void shouldThrowNullPointerExceptionWhenKeyIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.getMap(null, String.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("should throw IllegalArgumentException when key is blank")
    void shouldThrowWhenKeyIsBlank(String key) {
      assertThrows(IllegalArgumentException.class, () -> configuration.getMap(key, String.class));
    }

    @Test
    @DisplayName("should throw NullPointerException when class is null")
    void shouldThrowNullPointerExceptionWhenClassIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.getMap("env", null));
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      when(resolver.getMap(anyString(), any())).thenThrow(new ConversionException("bad map"));
      assertThrows(ConversionException.class, () -> configuration.getMap("env", String.class));
    }
  }

  @Nested
  @DisplayName("getRootAsMap() method")
  final class GetRootAsMapMethodTests {

    @Test
    @DisplayName("should delegate getRootAsMap to resolver.getRootAsMap()")
    void shouldDelegateGetRootAsMapToResolver() {
      Map<String, Object> expected = Map.of("clientA", "http://localhost");
      when(resolver.getRootAsMap()).thenReturn(expected);

      Map<String, Object> result = configuration.getRootAsMap();

      assertEquals(expected, result);
      verify(resolver).getRootAsMap();
    }

    @Test
    @DisplayName("should return emptyMap when resolver.getRootAsMap() returns empty map")
    void shouldReturnEmptyMapWhenResolverReturnsEmptyRootAsMap() {
      when(resolver.getRootAsMap()).thenReturn(Collections.emptyMap());

      Map<String, Object> result = configuration.getRootAsMap();

      assertTrue(result.isEmpty());
      assertSame(Collections.emptyMap(), result);
      verify(resolver).getRootAsMap();
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      when(resolver.getRootAsMap()).thenThrow(new ConversionException("invalid root"));
      assertThrows(ConversionException.class, () -> configuration.getRootAsMap());
    }
  }

  @Nested
  @DisplayName("getRootAs(Type) method")
  final class GetRootAsTypeMethodTests {

    @Test
    @DisplayName("should delegate getRootAs(Type) to resolver.getRootAs(Type) and return expected result")
    void shouldDelegateGetRootAsToResolver() {
      var type = Map.class;
      Optional<Map<String, Integer>> expected = Optional.of(Map.of("timeout", 1000));

      doReturn(expected).when(resolver).getRootAs(eq(type));

      Optional<Map<String, Integer>> result = configuration.getRootAs(type);

      assertEquals(expected, result);
      verify(resolver).getRootAs(eq(type));
    }

    @Test
    @DisplayName("should delegate getRootAs(Type) to resolver.getRootAs(Type) when mapping to a POJO record")
    void shouldDelegateGetRootAsToResolverWhenPojoRecord() {
      record AppConfig(String url, int timeout) {

      }
      var type = AppConfig.class;
      AppConfig expected = new AppConfig("http://localhost", 5000);
      Optional<AppConfig> expectedOptional = Optional.of(expected);

      doReturn(expectedOptional).when(resolver).getRootAs(eq(type));

      Optional<AppConfig> result = configuration.getRootAs(type);

      assertTrue(result.isPresent());
      assertEquals(expected, result.get());
      verify(resolver).getRootAs(eq(type));
    }

    @Test
    @DisplayName("should return empty Optional when resolver.getRootAs(Type) returns empty")
    void shouldReturnEmptyOptionalWhenResolverReturnsEmptyForGetRootAs() {
      var type = Map.class;
      when(resolver.getRootAs(eq(type))).thenReturn(Optional.empty());

      Optional<Map<String, Object>> result = configuration.getRootAs(type);

      assertTrue(result.isEmpty());
      verify(resolver).getRootAs(eq(type));
    }

    @Test
    @DisplayName("should throw NullPointerException when type is null")
    void shouldThrowNullPointerExceptionWhenTypeIsNull() {
      assertThrows(NullPointerException.class, () -> configuration.getRootAs(null));
    }

    @Test
    @DisplayName("should propagate ConversionException from resolver")
    void shouldPropagateConversionExceptionFromResolver() {
      var type = Map.class;
      when(resolver.getRootAs(eq(type))).thenThrow(new ConversionException("root failed"));
      assertThrows(ConversionException.class, () -> configuration.getRootAs(type));
    }
  }
}
