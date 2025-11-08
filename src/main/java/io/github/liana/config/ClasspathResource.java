package io.github.liana.config;

import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

final class ClasspathResource implements ResourceLocator {
  private final ClassLoader classLoader;
  private final Set<String> baseDirectories;

  public ClasspathResource() {
    this(ClasspathResource.class.getClassLoader(), List.of("", "config"));
  }

  public ClasspathResource(Collection<String> searchPaths) {
    this(ClasspathResource.class.getClassLoader(), searchPaths);
  }

  public ClasspathResource(ClassLoader classLoader, Collection<String> baseDirectories) {
    this.classLoader = requireNonNull(classLoader, "classLoader must not be null");
    this.baseDirectories = Collections.unmodifiableSet(
        new LinkedHashSet<>(requireNonNull(baseDirectories, "baseDirectories must not be null"))
    );
  }

  @Override
  public boolean resourceExists(String resourceName) {

    if (isBlank(resourceName)) {
      return false;
    }

    return baseDirectories.stream()
        .map(path -> buildPath(path, resourceName))
        .anyMatch(resourcePath -> classLoader.getResource(resourcePath) != null);
  }

  @Override
  public InputStream getResourceAsStream(String resourceName) {
    if (isBlank(resourceName)) {
      return null;
    }

    return baseDirectories.stream()
        .map(path -> buildPath(path, resourceName))
        .map(classLoader::getResourceAsStream)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private String buildPath(String path, String resourceName) {
    return path.isBlank() ? resourceName : path + "/" + resourceName;
  }
}
