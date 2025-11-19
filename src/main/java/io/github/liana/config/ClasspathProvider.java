package io.github.liana.config;

import static io.github.liana.internal.FilenameUtils.getExtension;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigProviderException;
import io.github.liana.internal.StringUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link ConfigProvider} implementation that loads configuration resources from the application's
 * classpath.
 *
 * <p>This provider searches for resources using one or more base directories (e.g. {@code ""} or
 * {@code "config"}) and attempts to resolve them either with the explicit extension provided in the
 * resource name or by trying a predefined list of default extensions ({@code .properties},
 * {@code .yaml}, {@code .yml}).
 *
 * <p>Resource lookup is performed using the configured {@link ClassLoader}. The first matching
 * resource found in the classpath is returned wrapped in a {@link ConfigResource}.
 *
 * <p><strong>Note:</strong> The caller is responsible for closing the input stream contained in
 * the returned {@code ConfigResource}.
 */
final class ClasspathProvider implements ConfigProvider {

  private static final List<String> DEFAULT_EXTENSIONS = List.of("properties", "yaml", "yml");

  private final ClassLoader classLoader;
  private final Set<String> baseDirectories;

  /**
   * Creates a new {@code ClasspathProvider} using the current thread’s context class loader and the
   * given base directories.
   *
   * @param baseDirectories the classpath directories to search (may be empty or null)
   */
  public ClasspathProvider(Collection<String> baseDirectories) {
    this(Thread.currentThread().getContextClassLoader(), baseDirectories);
  }

  /**
   * Creates a new {@code ClasspathProvider} using the given class loader and base directories.
   *
   * <p>If {@code baseDirectories} is null or empty, the provider defaults to searching in
   * {@code ""} (root) and {@code "config"} directories.
   *
   * @param classLoader     the class loader used to load classpath resources
   * @param baseDirectories the classpath directories to search
   * @throws NullPointerException if {@code classLoader} is null
   */
  public ClasspathProvider(ClassLoader classLoader, Collection<String> baseDirectories) {
    this.classLoader = requireNonNull(classLoader, "classLoader must not be null");
    this.baseDirectories = Collections.unmodifiableSet(
        new LinkedHashSet<>(normalizeBaseDirectories(baseDirectories, List.of("", "config")))
    );
  }

  private Collection<String> normalizeBaseDirectories(Collection<String> baseDirectories,
      Collection<String> defaultValue) {
    if (isNull(baseDirectories) || baseDirectories.isEmpty()) {
      return defaultValue;
    }
    return baseDirectories;
  }

  /**
   * Returns the provider identifier for classpath-backed resources.
   *
   * @return a singleton set containing the key {@code "classpath"}
   */
  @Override
  public Set<String> getKeys() {
    return Collections.singleton("classpath");
  }

  /**
   * Resolves a configuration resource by searching the classpath.
   *
   * <p>If the provided resource name already contains an extension, it is looked up directly.
   * Otherwise, each default extension is appended and attempted in order until a matching resource
   * is found.
   *
   * @param resource the reference describing the resource to resolve (must not be null)
   * @return a {@link ConfigResource} providing access to the underlying input stream
   * @throws NullPointerException    if {@code resource} or its name is null
   * @throws ConfigProviderException if no matching classpath resource is found
   */
  @Override
  public ConfigResource resolveResource(ConfigResourceReference resource) {
    validateResource(resource);
    String resourceName = resource.resourceName();
    if (hasExtension(resourceName)) {
      return getResource(resourceName)
          .orElseThrow(() ->
              new ConfigProviderException("config resource not found: " + resourceName));
    }

    return DEFAULT_EXTENSIONS.stream()
        .map(ext -> getResource(resourceName + "." + ext))
        .flatMap(Optional::stream)
        .findFirst()
        .orElseThrow(() -> new ConfigProviderException(
            "config resource not found with any default extension: " + resourceName
        ));
  }

  private boolean hasExtension(String resourceName) {
    return !StringUtils.isBlank(getExtension(resourceName));
  }

  /**
   * Attempts to locate a classpath resource by searching all configured base directories.
   *
   * @param resourceName the name of the resource to locate
   * @return an {@code Optional} containing a {@code ConfigResource} if found, otherwise empty
   */
  private Optional<ConfigResource> getResource(String resourceName) {
    return baseDirectories.stream()
        .map(directory -> buildPath(directory, resourceName))
        .map(classLoader::getResourceAsStream)
        .filter(Objects::nonNull)
        .findFirst()
        .map(inputStream -> new ConfigResource(resourceName, inputStream));
  }

  private String buildPath(String directory, String resourceName) {
    return directory.isBlank() ? resourceName : directory + "/" + resourceName;
  }
}
