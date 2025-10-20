package io.github.liana.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.liana.config.exception.ConfigProviderException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClasspathProviderTest {

  @Mock
  private ResourceLocator resourceLocator;
  private ClasspathProvider classpathProvider;

  @BeforeEach
  void setUp() {
    classpathProvider = new ClasspathProvider(resourceLocator);
  }

  @Test
  @DisplayName("should return singleton key 'classpath'")
  void shouldReturnClasspathKey() {
    Set<String> keys = classpathProvider.getKeys();
    assertEquals(Set.of("classpath"), keys);
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should resolve existing classpath resource successfully")
  void shouldResolveExistingClasspathResource() {
    String resourceName = "test.yml";
    InputStream input = new ByteArrayInputStream("content".getBytes());
    ConfigResourceReference reference = mock(ConfigResourceReference.class);

    when(reference.resourceName()).thenReturn(resourceName);
    when(resourceLocator.getResourceAsStream(resourceName)).thenReturn(input);

    ConfigResource result = classpathProvider.resolveResource(reference);

    assertNotNull(result);
    assertEquals(resourceName, result.resourceName());
    assertSame(input, result.inputStream());
    verify(resourceLocator).getResourceAsStream(resourceName);
  }

  @Test
  @DisplayName("should throw NullPointerException when resource is null")
  void shouldThrowWhenResourceIsNull() {
    assertThrows(NullPointerException.class, () -> classpathProvider.resolveResource(null));
    verifyNoInteractions(resourceLocator);
  }

  @Test
  @DisplayName("should throw NullPointerException when resource name is null")
  void shouldThrowWhenResourceNameIsNull() {
    ConfigResourceReference reference = mock(ConfigResourceReference.class);
    when(reference.resourceName()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> classpathProvider.resolveResource(reference));
    verify(reference).resourceName();
    verifyNoInteractions(resourceLocator);
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should throw ConfigProviderException when classpath resource not found")
  void shouldThrowWhenClasspathResourceNotFound() {
    String resourceName = "test.yml";

    ConfigResourceReference reference = mock(ConfigResourceReference.class);
    when(reference.resourceName()).thenReturn(resourceName);
    when(resourceLocator.getResourceAsStream(resourceName)).thenReturn(null);

    ConfigProviderException exception =
        assertThrows(ConfigProviderException.class,
            () -> classpathProvider.resolveResource(reference));

    assertEquals("config resource not found " + resourceName, exception.getMessage());
    verify(resourceLocator).getResourceAsStream(resourceName);
  }
}
