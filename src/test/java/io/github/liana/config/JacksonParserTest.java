package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liana.config.exception.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JacksonParserTest {

  private JacksonParser parser;

  @BeforeEach
  void setUp() {
    parser = new JacksonParser(new ObjectMapper());
  }

  @Test
  @DisplayName("should throw NullPointerException when ObjectMapper is null")
  void shouldThrowNullPointerExceptionWhenObjectMapperIsNull() {
    assertThrows(NullPointerException.class, () -> new JacksonParser(null));
  }

  @Test
  @DisplayName("should create configuration successfully from valid JSON input stream")
  void shouldCreateConfigurationSuccessfully() throws IOException {
    String json = "{\"app\": {\"name\": \"liana\", \"version\": 1}}";
    InputStream input = new ByteArrayInputStream(json.getBytes());

    Configuration configuration = parser.parse(input);

    assertNotNull(configuration);
    assertInstanceOf(JacksonConfiguration.class, configuration);
    assertTrue(configuration.containsKey("app.name"));
    assertEquals("liana", configuration.get("app.name", String.class).orElse(null));
    assertEquals(1, configuration.get("app.version", Integer.class).orElse(-1));
  }

  @Test
  @DisplayName("should propagate ConversionException when input JSON is invalid")
  void shouldPropagateConversionExceptionWhenInputJsonIsInvalid() {
    String invalidJson = "{ invalid json ";
    InputStream input = new ByteArrayInputStream(invalidJson.getBytes());

    assertThrows(ConversionException.class, () -> parser.parse(input));
  }

  @Test
  @DisplayName("should throw NullPointerException when input is null")
  void shouldThrowExceptionWhenInputIsNull() {
    assertThrows(NullPointerException.class, () -> parser.parse(null));
  }
}
