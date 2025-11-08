package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JacksonConfigurationTest {

  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
  }

  @Test
  @DisplayName("should create configuration successfully from valid JSON input stream")
  void shouldCreateConfigurationSuccessfully() {
    String json = "{\"app\": {\"name\": \"liana\", \"version\": 1}}";
    InputStream input = new ByteArrayInputStream(json.getBytes());

    JacksonConfiguration config = new JacksonConfiguration(mapper, input);

    assertNotNull(config);
    assertTrue(config.containsKey("app.name"));
    assertEquals("liana", config.get("app.name", String.class).orElse(null));
    assertEquals(1, config.get("app.version", Integer.class).orElse(-1));
  }

  @Test
  @DisplayName("should return empty optional for missing keys")
  void shouldReturnEmptyOptionalForMissingKeys() {
    String json = "{\"app\": {\"name\": \"liana\"}}";
    InputStream input = new ByteArrayInputStream(json.getBytes());

    JacksonConfiguration config = new JacksonConfiguration(mapper, input);

    assertFalse(config.containsKey("app.unknown"));
    assertTrue(config.get("app.unknown", String.class).isEmpty());
  }

  @Test
  @DisplayName("should read list of values correctly")
  void shouldReadListOfValuesCorrectly() {
    String json = "{\"servers\": [\"alpha\", \"beta\", \"gamma\"]}";
    InputStream input = new ByteArrayInputStream(json.getBytes());

    JacksonConfiguration config = new JacksonConfiguration(mapper, input);

    var servers = config.getList("servers", String.class);
    assertEquals(3, servers.size());
    assertEquals("alpha", servers.get(0));
    assertEquals("beta", servers.get(1));
    assertEquals("gamma", servers.get(2));
  }

  @Test
  @DisplayName("should read nested map values correctly")
  void shouldReadNestedMapValuesCorrectly() {
    String json = "{\"database\": {\"host\": \"localhost\", \"port\": 5432}}";
    InputStream input = new ByteArrayInputStream(json.getBytes());

    JacksonConfiguration config = new JacksonConfiguration(mapper, input);

    Map<String, Object> db = config.getMap("database", Object.class);
    assertEquals("localhost", db.get("host"));
    assertEquals(5432, db.get("port"));
  }

  @Test
  @DisplayName("should get root configuration as map")
  void shouldGetRootConfigurationAsMap() {
    String json = "{\"key\": \"value\"}";
    InputStream input = new ByteArrayInputStream(json.getBytes());

    JacksonConfiguration config = new JacksonConfiguration(mapper, input);

    Map<String, Object> root = config.getRootAsMap();
    assertEquals("value", root.get("key"));
  }

  @Test
  @DisplayName("should throw NullPointerException when mapper is null")
  void shouldThrowExceptionWhenMapperIsNull() {
    InputStream input = new ByteArrayInputStream("{}".getBytes());
    assertThrows(NullPointerException.class, () -> new JacksonConfiguration(null, input));
  }

  @Test
  @DisplayName("should throw NullPointerException when input is null")
  void shouldThrowExceptionWhenInputIsNull() {
    assertThrows(NullPointerException.class, () -> new JacksonConfiguration(mapper, null));
  }
}
