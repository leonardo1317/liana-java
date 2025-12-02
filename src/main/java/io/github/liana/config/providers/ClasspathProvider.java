package io.github.liana.config.providers;

import static io.github.liana.config.internal.FilenameUtils.getExtension;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.core.DefaultResourceStream;
import io.github.liana.config.core.ResourceIdentifier;
import io.github.liana.config.core.ResourceStream;
import io.github.liana.config.core.exception.ResourceProviderException;
import io.github.liana.config.spi.ResourceProvider;
import io.github.liana.config.internal.StringUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Provides configuration resources from the classpath.
 *
 * <p>This implementation of {@link ResourceProvider} resolves configuration resources
 * from one or more base directories in the classpath. If a resource name does not have an
 * extension, a set of default extensions ("properties", "yaml", "yml") is attempted in order.
 *
 * <p>Instances are immutable and thread-safe.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Locate a configuration resource on the classpath.</li>
 *   <li>Apply default extensions if the resource name does not specify one.</li>
 *   <li>Validate that resource identifiers are non-null and contain a name.</li>
 * </ul>
 *
 * <p>Exposed exceptions:
 * <ul>
 *   <li>{@link ResourceProviderException} if a resource cannot be found.</li>
 *   <li>{@link NullPointerException} if a resource identifier is null.</li>
 * </ul>
 */
public class ClasspathProvider implements ResourceProvider {

  private static final List<String> DEFAULT_EXTENSIONS = List.of("properties", "yaml", "yml");

  private final ClassLoader classLoader;
  private final Set<String> baseDirectories;

  /**
   * Creates a new classpath provider using the current thread's context class loader.
   *
   * @param baseDirectories collection of base directories to search; if null or empty, defaults to
   *                        ["", "config"]
   * @throws NullPointerException if baseDirectories contains null elements
   */
  public ClasspathProvider(Collection<String> baseDirectories) {
    this(Thread.currentThread().getContextClassLoader(), baseDirectories);
  }

  /**
   * Creates a new classpath provider using the specified class loader.
   *
   * @param classLoader     the class loader to use; must not be {@code null}
   * @param baseDirectories collection of base directories to search; if null or empty, defaults to
   *                        ["", "config"]
   * @throws NullPointerException if {@code classLoader} is null
   */
  public ClasspathProvider(ClassLoader classLoader, Collection<String> baseDirectories) {
    this.classLoader = requireNonNull(classLoader, "classLoader must not be null");
    this.baseDirectories = Collections.unmodifiableSet(
        new LinkedHashSet<>(defaultIfEmptyBaseDirectories(baseDirectories, List.of("", "config")))
    );
  }

  private Collection<String> defaultIfEmptyBaseDirectories(Collection<String> baseDirectories,
      Collection<String> defaultValue) {
    if (isNull(baseDirectories) || baseDirectories.isEmpty()) {
      return defaultValue;
    }
    return baseDirectories;
  }

  @Override
  public Set<String> getKeys() {
    return Collections.singleton("classpath");
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation attempts to resolve the resource from the configured base directories.
   * If the resource has no extension, it tries default extensions: "properties", "yaml", "yml".
   */
  @Override
  public ResourceStream resolveResource(ResourceIdentifier resource) {
    validateResource(resource);
    String resourceName = resource.resourceName();
    if (hasExtension(resourceName)) {
      return getResource(resourceName)
          .orElseThrow(() ->
              new ResourceProviderException("config resource not found: " + resourceName));
    }

    return DEFAULT_EXTENSIONS.stream()
        .map(ext -> getResource(resourceName + "." + ext))
        .flatMap(Optional::stream)
        .findFirst()
        .orElseThrow(() -> new ResourceProviderException(
            "config resource not found with any default extension: " + resourceName
        ));
  }

  private boolean hasExtension(String resourceName) {
    return !StringUtils.isBlank(getExtension(resourceName));
  }

  private Optional<ResourceStream> getResource(String resourceName) {
    return baseDirectories.stream()
        .map(directory -> buildPath(directory, resourceName))
        .map(classLoader::getResourceAsStream)
        .filter(Objects::nonNull)
        .findFirst()
        .map(inputStream -> new DefaultResourceStream(resourceName, inputStream));
  }

  private String buildPath(String directory, String resourceName) {
    return directory.isBlank() ? resourceName : directory + "/" + resourceName;
  }
}
