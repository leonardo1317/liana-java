package io.github.liana.config;

import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.internal.FilenameUtils;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves configuration resources based on allowed file extensions.
 *
 * <p>This class determines whether a resource name has a supported extension and locates
 * configuration files either with or without an explicit extension. If no extension is provided, it
 * automatically tries all allowed extensions until a matching resource is found.
 *
 * <p>All allowed extensions and lookups are immutable and thread-safe.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * Set<String> allowed = Set.of("yaml", "json");
 * ResourceLocator locator = new ClasspathResourceLocator();
 * ResourceExtensionResolver resolver = new ResourceExtensionResolver(allowed, locator);
 *
 * Optional<String> resource = resolver.findConfigResource("config/settings");
 * // → Might return "config/settings.yaml" if present in classpath
 * }</pre>
 */
public class ResourceExtensionResolver {

  private final ConfigLogger log;
  private final Set<String> allowedExtensions;
  private final ResourceLocator resourceLocator;

  /**
   * Creates a new {@code ResourceExtensionResolver} with the given allowed extensions and locator.
   *
   * @param allowedExtensions the set of supported file extensions (e.g., {@code "yaml"},
   *                          {@code "json"})
   * @param resourceLocator   the locator used to check for resource existence
   * @throws NullPointerException if any argument is {@code null}
   */
  public ResourceExtensionResolver(Set<String> allowedExtensions, ResourceLocator resourceLocator) {
    this.allowedExtensions = Collections.unmodifiableSet(requireNonNull(allowedExtensions,
        "allowedExtensions must not be null"));
    this.resourceLocator = requireNonNull(resourceLocator, "resourceLocator must not be null");
    this.log = ConsoleConfigLogger.getLogger();
  }

  /**
   * Checks whether the specified resource name has an allowed extension.
   *
   * <p>This method extracts the extension from the resource name and verifies that it
   * belongs to the allowed set. If the resource name is blank or has no extension, the method
   * returns {@code false}.
   *
   * @param resourceName the resource name to validate
   * @return {@code true} if the extension is allowed; {@code false} otherwise
   */
  public boolean isExtensionAllowed(String resourceName) {
    if (isBlank(resourceName)) {
      return false;
    }

    return allowedExtensions.contains(getExtension(resourceName));
  }

  /**
   * Attempts to locate a configuration resource by name.
   *
   * <p>If the provided resource name already includes a valid extension, it is verified
   * directly. Otherwise, this method tries appending each allowed extension in order until a
   * matching resource is found.
   *
   * @param resourceName the base resource name (with or without extension)
   * @return an {@link Optional} containing the resolved resource name if found, or an empty
   * {@link Optional} if no valid resource exists
   */
  public Optional<String> findConfigResource(String resourceName) {
    if (isBlank(resourceName)) {
      return Optional.empty();
    }

    String currentExt = getExtension(resourceName);
    if (!isBlank(currentExt)) {
      return resolveResourceWithExtension(resourceName, currentExt);
    }

    return allowedExtensions.stream()
        .map(extension -> resourceName + "." + extension)
        .filter(resourceLocator::resourceExists)
        .findFirst()
        .or(() -> {
          log.warn(() -> "no standard config file found in classpath for: " + resourceName);
          return Optional.empty();
        });
  }

  private String getExtension(String resourceName) {

    String fileName = FilenameUtils.getName(resourceName);

    return FilenameUtils.getExtension(fileName).toLowerCase(Locale.ROOT);
  }

  private Optional<String> resolveResourceWithExtension(String resourceName, String extension) {
    if (!allowedExtensions.contains(extension)) {
      log.warn(() -> "unsupported extension for resource: " + resourceName);
      return Optional.empty();
    }

    if (resourceLocator.resourceExists(resourceName)) {
      return Optional.of(resourceName);
    }

    log.warn(() -> "allowed extension but resource not found: " + resourceName);
    return Optional.empty();
  }
}
