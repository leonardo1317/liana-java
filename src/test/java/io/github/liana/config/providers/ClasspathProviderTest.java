package io.github.liana.config.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.liana.config.core.ResourceIdentifier;
import io.github.liana.config.core.ResourceStream;
import io.github.liana.config.core.exception.ResourceProviderException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
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
  private ClassLoader classLoader;

  @Mock
  private ResourceIdentifier resourceIdentifier;

  private ClasspathProvider classpathProvider;

  @BeforeEach
  void setUp() {
    classpathProvider = new ClasspathProvider(classLoader, List.of(""));
  }

  @Test
  @DisplayName("should return singleton key 'classpath'")
  void shouldReturnClasspathKey() {
    Set<String> keys = classpathProvider.getKeys();
    assertEquals(Set.of("classpath"), keys);
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should resolve resource when resource name includes extension")
  void shouldResolveResourceWithExplicitExtension() {
    String resourceName = "test.yml";
    InputStream expectedStream = new ByteArrayInputStream("content".getBytes());

    when(resourceIdentifier.resourceName()).thenReturn(resourceName);
    when(classLoader.getResourceAsStream(resourceName)).thenReturn(expectedStream);

    ResourceStream result = classpathProvider.resolveResource(resourceIdentifier);

    assertNotNull(result);
    assertEquals(resourceName, result.name());
    assertSame(expectedStream, result.stream());

    verify(classLoader).getResourceAsStream(resourceName);
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should try default extensions when no extension in resource name")
  void shouldTryDefaultExtensions() {
    String resourceName = "test";
    String resolved = "test.yaml";

    when(resourceIdentifier.resourceName()).thenReturn(resourceName);
    when(classLoader.getResourceAsStream("test.properties")).thenReturn(null);
    when(classLoader.getResourceAsStream("test.yaml"))
        .thenReturn(new ByteArrayInputStream("abc".getBytes()));

    ResourceStream result = classpathProvider.resolveResource(resourceIdentifier);

    assertNotNull(result);
    assertEquals(resolved, result.name());

    verify(classLoader).getResourceAsStream("test.properties");
    verify(classLoader).getResourceAsStream("test.yaml");
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should throw when resource not found in classpath")
  void shouldThrowWhenResourceNotFound() {
    String resourceName = "notfound.yml";

    when(resourceIdentifier.resourceName()).thenReturn(resourceName);
    when(classLoader.getResourceAsStream(resourceName)).thenReturn(null);

    ResourceProviderException ex = assertThrows(
        ResourceProviderException.class,
        () -> classpathProvider.resolveResource(resourceIdentifier)
    );

    assertEquals("config resource not found: " + resourceName, ex.getMessage());

    verify(classLoader).getResourceAsStream(resourceName);
  }

  @Test
  @DisplayName("should throw NullPointerException when identifier is null")
  void shouldThrowWhenIdentifierIsNull() {
    assertThrows(NullPointerException.class, () -> classpathProvider.resolveResource(null));
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when resource name is null")
  void shouldThrowWhenResourceNameNull() {
    when(resourceIdentifier.resourceName()).thenReturn(null);

    assertThrows(IllegalArgumentException.class,
        () -> classpathProvider.resolveResource(resourceIdentifier));

    verify(resourceIdentifier).resourceName();
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when resource name is blank")
  void shouldThrowWhenResourceNameBlank() {
    when(resourceIdentifier.resourceName()).thenReturn("");

    assertThrows(IllegalArgumentException.class,
        () -> classpathProvider.resolveResource(resourceIdentifier));

    verify(resourceIdentifier).resourceName();
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("single-arg constructor should use Thread context class loader")
  void constructorShouldUseThreadContextClassLoader() {
    ClassLoader original = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(classLoader);
      ClasspathProvider providerUsingThreadCL = new ClasspathProvider(List.of(""));
      String resourceName = "thread-test.yml";
      InputStream stream = new ByteArrayInputStream("x".getBytes());

      when(resourceIdentifier.resourceName()).thenReturn(resourceName);
      when(classLoader.getResourceAsStream(resourceName)).thenReturn(stream);

      ResourceStream result = providerUsingThreadCL.resolveResource(resourceIdentifier);

      assertNotNull(result);
      assertSame(stream, result.stream());
      verify(classLoader).getResourceAsStream(resourceName);

    } finally {
      Thread.currentThread().setContextClassLoader(original);
    }
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("constructor with only baseDirectories should use provided classloader and directories")
  void shouldConstructWithOnlyBaseDirectories() {
    ClassLoader loader = mock(ClassLoader.class);
    ClasspathProvider provider = new ClasspathProvider(loader, List.of("config", "app"));

    when(resourceIdentifier.resourceName()).thenReturn("test.yml");

    when(loader.getResourceAsStream("config/test.yml")).thenReturn(null);
    when(loader.getResourceAsStream("app/test.yml")).thenReturn(null);

    assertThrows(ResourceProviderException.class, () -> provider.resolveResource(resourceIdentifier));

    verify(loader).getResourceAsStream("config/test.yml");
    verify(loader).getResourceAsStream("app/test.yml");
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("constructor should default baseDirectories when null")
  void shouldUseDefaultBaseDirectoriesWhenNull() {
    ClassLoader loader = mock(ClassLoader.class);
    ClasspathProvider provider = new ClasspathProvider(loader, null);

    when(resourceIdentifier.resourceName()).thenReturn("file.yml");
    when(loader.getResourceAsStream("file.yml")).thenReturn(null);
    when(loader.getResourceAsStream("config/file.yml")).thenReturn(null);

    assertThrows(ResourceProviderException.class, () -> provider.resolveResource(resourceIdentifier));

    verify(loader).getResourceAsStream("file.yml");
    verify(loader).getResourceAsStream("config/file.yml");
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("constructor should default baseDirectories when empty")
  void shouldUseDefaultBaseDirectoriesWhenEmpty() {
    ClassLoader loader = mock(ClassLoader.class);
    ClasspathProvider provider = new ClasspathProvider(loader, List.of());

    when(resourceIdentifier.resourceName()).thenReturn("file.yml");
    when(loader.getResourceAsStream("file.yml")).thenReturn(null);
    when(loader.getResourceAsStream("config/file.yml")).thenReturn(null);

    assertThrows(ResourceProviderException.class, () -> provider.resolveResource(resourceIdentifier));

    verify(loader).getResourceAsStream("file.yml");
    verify(loader).getResourceAsStream("config/file.yml");
  }

  @Test
  @SuppressWarnings("resource")
  @DisplayName("should throw ResourceProviderException when no default extension resolves resource")
  void shouldThrowWhenNoDefaultExtensionResolvesResource() {
    String resourceName = "missing";

    when(resourceIdentifier.resourceName()).thenReturn(resourceName);
    when(classLoader.getResourceAsStream("missing.properties")).thenReturn(null);
    when(classLoader.getResourceAsStream("missing.yaml")).thenReturn(null);
    when(classLoader.getResourceAsStream("missing.yml")).thenReturn(null);

    ResourceProviderException ex = assertThrows(ResourceProviderException.class,
        () -> classpathProvider.resolveResource(resourceIdentifier));

    assertEquals(
        "config resource not found with any default extension: " + resourceName,
        ex.getMessage()
    );

    verify(classLoader).getResourceAsStream("missing.properties");
    verify(classLoader).getResourceAsStream("missing.yaml");
    verify(classLoader).getResourceAsStream("missing.yml");
  }
}
