package io.github.liana.config.loaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.ResourceParser;
import io.github.liana.config.core.exception.ResourceLoaderException;
import io.github.liana.config.spi.ResourceLoader;
import io.github.liana.config.core.FileFormat;
import io.github.liana.config.core.DefaultResourceStream;
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
class XmlLoaderTest {

  @Mock
  private DefaultResourceStream resource;

  @Mock
  private ResourceParser resourceParser;

  private ResourceLoader loader;

  @BeforeEach
  void setUp() {
    loader = new XmlLoader(resourceParser);
  }

  @Test
  @DisplayName("should throw NullPointerException when ResourceParser is null")
  void shouldThrowExceptionWhenConfigParserIsNull() {
    assertThrows(NullPointerException.class, () -> new YamlLoader(null));
  }

  @Test
  @DisplayName("should return XML as supported file format")
  void shouldReturnXmlAsSupportedFileFormat() {
    assertEquals(FileFormat.XML.getExtensions(), loader.getKeys());
  }

  @Test
  @DisplayName("should load valid XML configuration successfully")
  void shouldLoadValidXmlConfigurationSuccessfully() throws IOException {
    String content = "<root><key>value</key></root>";
    InputStream input = new ByteArrayInputStream(content.getBytes());
    Configuration configuration = mock(Configuration.class);

    when(resource.stream()).thenReturn(input);
    when(resource.name()).thenReturn("test.xml");
    when(configuration.get(anyString(),  eq(String.class))).thenReturn(Optional.of("value"));
    when(resourceParser.parse(input)).thenReturn(configuration);

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
    when(resource.stream()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> loader.load(resource));
  }

  @Test
  @DisplayName("should throw NullPointerException when resource name is null")
  void shouldThrowNullPointerExceptionWhenResourceNameIsNull() {
    String content = "<root><key>value</key></root>";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.stream()).thenReturn(input);
    when(resource.name()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> loader.load(resource));
  }

  @Test
  @DisplayName("should throw ResourceLoaderException when XML is malformed")
  void shouldThrowConfigLoaderExceptionWhenXmlIsMalformed() throws IOException {
    String content = "<root><key>value<key></root>";
    InputStream input = new ByteArrayInputStream(content.getBytes());

    when(resource.stream()).thenReturn(input);
    when(resource.name()).thenReturn("malformed.xml");
    when(resourceParser.parse(input)).thenThrow(new IOException("Malformed XML"));

    assertThrows(ResourceLoaderException.class, () -> loader.load(resource));
  }
}
