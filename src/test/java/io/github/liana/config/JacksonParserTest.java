package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JacksonParserTest {

  @Mock
  private ObjectMapper mapper;

  @Mock
  private InputStream inputStream;

  private JacksonParser parser;

  @BeforeEach
  void setUp() {
    parser = new JacksonParser(mapper);
  }

  @Test
  @DisplayName("should throw NullPointerException when ObjectMapper is null")
  void shouldThrowNullPointerExceptionWhenObjectMapperIsNull() {
    assertThrows(NullPointerException.class, () -> new JacksonParser(null));
  }

  @Test
  @DisplayName("should parse input stream and return JacksonConfiguration")
  void shouldParseInputStreamAndReturnJacksonConfiguration() throws IOException {
    Map<String, Object> expectedMap = Map.of("key", "value");

    when(
        mapper.readValue(any(InputStream.class), Mockito.<TypeReference<Map<String, Object>>>any()))
        .thenReturn(expectedMap);

    Configuration configuration = parser.parse(inputStream);

    assertNotNull(configuration);
    assertInstanceOf(JacksonConfiguration.class, configuration);
  }

  @Test
  @DisplayName("should propagate IOException when parsing fails")
  void shouldPropagateIOExceptionWhenParsingFails() throws IOException {
    when(
        mapper.readValue(any(InputStream.class), Mockito.<TypeReference<Map<String, Object>>>any()))
        .thenThrow(new IOException("error reading JSON"));

    assertThrows(IOException.class, () -> parser.parse(inputStream));
  }
}
