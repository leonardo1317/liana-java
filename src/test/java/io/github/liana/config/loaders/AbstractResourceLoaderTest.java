package io.github.liana.config.loaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.DefaultResourceStream;
import io.github.liana.config.core.ResourceParser;
import io.github.liana.config.core.exception.ResourceLoaderException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractResourceLoaderTest {

  @Mock
  private ResourceParser parser;

  @Mock
  private DefaultResourceStream resource;

  @Mock
  private Configuration configuration;

  private TestLoader loader;

  @BeforeEach
  void setUp() {
    loader = new TestLoader(parser);
  }

  private static class TestLoader extends AbstractResourceLoader {

    protected TestLoader(ResourceParser parser) {
      super(parser);
    }

    @Override
    protected Set<String> getSupportedExtensions() {
      return Set.of("test");
    }
  }

  @Test
  @DisplayName("should throw NullPointerException when parser is null")
  void shouldThrowWhenParserIsNull() {
    assertThrows(NullPointerException.class, () -> new TestLoader(null));
  }

  @Test
  @DisplayName("should return supported keys")
  void shouldReturnSupportedKeys() {
    Set<String> keys = loader.getKeys();
    assertNotNull(keys);
    assertTrue(keys.contains("test"));
  }

  @Test
  @DisplayName("should load configuration successfully")
  void shouldLoadConfigurationSuccessfully() throws IOException {
    InputStream inputStream = new ByteArrayInputStream("config-data".getBytes());
    when(resource.stream()).thenReturn(inputStream);
    when(resource.name()).thenReturn("test.test");
    when(parser.parse(inputStream)).thenReturn(configuration);

    Configuration result = loader.load(resource);

    assertNotNull(result);
    assertEquals(configuration, result);
    verify(parser).parse(inputStream);
  }

  @Test
  @DisplayName("should throw ResourceLoaderException when parser fails")
  void shouldThrowConfigLoaderExceptionOnParserFailure() throws IOException {
    when(resource.stream()).thenReturn(mock(InputStream.class));
    when(resource.name()).thenReturn("fail.test");
    when(parser.parse(any(InputStream.class)))
        .thenThrow(new IOException("io error"));

    ResourceLoaderException exception = assertThrows(ResourceLoaderException.class,
        () -> loader.load(resource));

    assertTrue(exception.getMessage().contains("fail.test"));
    assertInstanceOf(IOException.class, exception.getCause());
  }

  @Test
  @DisplayName("should throw NullPointerException when resource is null")
  void shouldThrowWhenResourceIsNull() {
    assertThrows(NullPointerException.class, () -> loader.load(null));
  }
}
