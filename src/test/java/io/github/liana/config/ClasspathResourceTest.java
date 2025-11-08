package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClasspathResourceTest {

  @Mock
  private ClassLoader classLoader;

  private ResourceLocator resource;

  @BeforeEach
  void setUp() {
    resource = new ClasspathResource();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should return false when resource name is blank")
  void shouldReturnFalseWhenResourceNameIsBlank(String resourceName) {
    assertFalse(resource.resourceExists(resourceName));
  }

  @Test
  @DisplayName("should return false when resource name is null")
  void shouldReturnFalseWhenResourceNameIsNull() {
    assertFalse(resource.resourceExists(null));
  }

  @Test
  @DisplayName("should return true when resource is found at root path")
  void shouldReturnTrueWhenResourceFoundAtRootPath() {
    URL mockUrl = mock(URL.class);
    when(classLoader.getResource("test.yml")).thenReturn(mockUrl);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    assertTrue(resource.resourceExists("test.yml"));

    verify(classLoader, never()).getResource("config/test.yml");
  }

  @Test
  @DisplayName("should return true when resource found in config path")
  void shouldReturnTrueWhenResourceFoundInConfigPath() {
    URL mockUrl = mock(URL.class);

    when(classLoader.getResource("test.yml")).thenReturn(null);
    when(classLoader.getResource("config/test.yml")).thenReturn(mockUrl);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    assertTrue(resource.resourceExists("test.yml"));
    verify(classLoader).getResource("config/test.yml");
  }

  @Test
  @DisplayName("should return false when resource not found in any path")
  void shouldReturnFalseWhenResourceNotFoundInAnyPath() {
    when(classLoader.getResource(anyString())).thenReturn(null);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    assertFalse(resource.resourceExists("test.yml"));
    verify(classLoader, times(2)).getResource(anyString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n"})
  @DisplayName("should return null when resource name is blank")
  void shouldReturnNullWhenResourceNameIsBlankOrNull(String resourceName) {
    assertNull(resource.getResourceAsStream(resourceName));
  }

  @Test
  @DisplayName("should return null when resource name is null")
  void shouldReturnNullWhenResourceNameIsNull() {
    assertNull(resource.getResourceAsStream(null));
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should return InputStream when resource found at root path")
  void shouldReturnInputStreamWhenResourceFoundAtRootPath() {
    InputStream mockStream = new ByteArrayInputStream("mock-content".getBytes());

    when(classLoader.getResourceAsStream("test.yml")).thenReturn(mockStream);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    InputStream result = resource.getResourceAsStream("test.yml");

    assertNotNull(result);
    assertEquals(mockStream, result);

    verify(classLoader, never()).getResourceAsStream("config/test.yml");
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should return InputStream when resource found in config path")
  void shouldReturnInputStreamWhenResourceFoundInConfigPath() {
    InputStream mockStream = new ByteArrayInputStream("mock-content".getBytes());

    when(classLoader.getResourceAsStream("test.yml")).thenReturn(null);
    when(classLoader.getResourceAsStream("config/test.yml")).thenReturn(mockStream);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    InputStream result = resource.getResourceAsStream("test.yml");

    assertNotNull(result);
    assertEquals(mockStream, result);
    verify(classLoader).getResourceAsStream("config/test.yml");
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should return null when resource not found in any search path")
  void shouldReturnNullWhenResourceNotFoundInAnySearchPath() {
    when(classLoader.getResourceAsStream(anyString())).thenReturn(null);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    assertNull(resource.getResourceAsStream("test.yml"));
    verify(classLoader, times(2)).getResourceAsStream(anyString());
  }

  @Test
  @DisplayName("should check root path before config path when searching for a resource")
  void shouldCheckRootPathBeforeConfigPath() {
    URL mockUrl = mock(URL.class);

    when(classLoader.getResource("test.yml")).thenReturn(null);
    when(classLoader.getResource("config/test.yml")).thenReturn(mockUrl);

    resource = new ClasspathResource(classLoader, List.of("", "config"));

    resource.resourceExists("test.yml");

    InOrder inOrder = inOrder(classLoader);
    inOrder.verify(classLoader).getResource("test.yml");
    inOrder.verify(classLoader).getResource("config/test.yml");
  }
}
