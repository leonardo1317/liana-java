package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigProviderException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Provides configuration resources from the application's classpath.
 *
 * <p>This implementation handles resources located in the classpath, typically files stored in
 * {@code src/main/resources} or bundled in JAR files.
 */
final class ClasspathProvider implements ConfigProvider {

  private final ResourceLocator resourceLocator;

  ClasspathProvider(ResourceLocator resourceLocator) {
    this.resourceLocator = requireNonNull(resourceLocator, "resourceLocator must not be null");
  }

  /**
   * Returns the provider identifier for classpath resources.
   *
   * @return The constant string "classpath" identifying this provider type.
   */
  @Override
  public Set<String> getKeys() {
    return Collections.singleton("classpath");
  }

  /**
   * Resolves a classpath resource into a loadable configuration resource.
   *
   * @param resource The resolved resource descriptor containing the resource name (must not be
   *                 null)
   * @return A {@link ConfigResource} with an open input stream to the classpath resource
   * @throws NullPointerException    If the resource or its name is null
   * @throws ConfigProviderException If the resource cannot be found in the classpath
   * @implNote The caller is responsible for closing the returned resource's input stream
   */
  @Override
  public ConfigResource resolveResource(ConfigResourceReference resource) {
    validateResource(resource);
    InputStream input = resourceLocator.getResourceAsStream(resource.resourceName());
    if (input == null) {
      throw new ConfigProviderException("config resource not found " + resource.resourceName());
    }
    return new ConfigResource(resource.resourceName(), input);
  }
}
