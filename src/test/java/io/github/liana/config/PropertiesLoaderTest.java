package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigLoaderException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertiesLoaderTest {

  @Mock
  private ConfigResource resource;

  @Mock
  private ConfigParser configParser;

  private ConfigLoader loader;

  @BeforeEach
  void setUp() {
    loader = new PropertiesLoader(configParser);
  }

  @Test
  @DisplayName("should throw NullPointerException when ConfigParser is null")
  void shouldThrowExceptionWhenConfigParserIsNull() {
    assertThrows(NullPointerException.class, () -> new YamlLoader(null));
  }

  @Test
  @DisplayName("should return Properties as supported file format")
  void shouldReturnPropertiesAsSupportedFileFormat() {
    assertEquals(ConfigFileFormat.PROPERTIES.getExtensions(), loader.getKeys());
  }

  @Test
  @DisplayName("should load valid Properties configuration successfully")
  void shouldLoadValidPropertiesConfigurationSuccessfully() throws IOException {
    String content = "key=value";
    InputStream input = new ByteArrayInputStream(content.getBytes());
    Configuration configuration = mock(Configuration.class);

    when(resource.inputStream()).thenReturn(input);
    when(resource.resourceName()).thenReturn("test.properties");
    when(configuration.get(anyString(),  eq(String.class))).thenReturn(Optional.of("value"));
    when(configParser.parse(input)).thenReturn(configuration);

    Configuration config = loader.load(resource);

    assertNotNull(config);
    assertEquals(Optional.of("value"), config.get("key", String.class));
  }

  @Test
  @DisplayName("should throw NullPointerException when resource is null")
  void shouldThrowNullPointerExceptionWhenResourceIsNull() {
    assertThrows(NullPointerException.class, () -> loader.load(null));
  }

  @Test
  @DisplayName("should throw NullPointerException when input stream is null")
  void shouldThrowNullPointerExceptionWhenInputStreamIsNull() {
    when(resource.inputStream()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> loader.load(resource));
  }

  @Test
  @DisplayName("should throw NullPointerException when resource name is null")
  void shouldThrowNullPointerExceptionWhenResourceNameIsNull() {
    String content = "key=value";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.inputStream()).thenReturn(input);
    when(resource.resourceName()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> loader.load(resource));
  }

  @Test
  @DisplayName("should throw ConfigLoaderException when Properties is malformed")
  void shouldThrowConfigLoaderExceptionWhenPropertiesIsMalformed() throws IOException {
    String content = "key=value";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.inputStream()).thenReturn(input);
    when(resource.resourceName()).thenReturn("malformed.properties");
    when(configParser.parse(input)).thenThrow(new IOException("Malformed properties"));

    assertThrows(ConfigLoaderException.class, () -> loader.load(resource));
  }
}
