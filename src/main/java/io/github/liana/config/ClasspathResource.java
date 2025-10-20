package io.github.liana.config;

import static io.github.liana.internal.StringUtils.isBlank;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

final class ClasspathResource implements ResourceLocator {

  private static final Set<String> DEFAULT_SEARCH_PATHS = Collections.unmodifiableSet(
      new LinkedHashSet<>(List.of("", "config")));
  private final ClassLoader classLoader;

  public ClasspathResource() {
    this(ClasspathResource.class.getClassLoader());
  }

  public ClasspathResource(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public boolean resourceExists(String resourceName) {

    if (isBlank(resourceName)) {
      return false;
    }

    return DEFAULT_SEARCH_PATHS.stream()
        .map(path -> buildPath(path, resourceName))
        .anyMatch(resourcePath -> classLoader.getResource(resourcePath) != null);
  }

  @Override
  public InputStream getResourceAsStream(String resourceName) {
    if (isBlank(resourceName)) {
      return null;
    }

    return DEFAULT_SEARCH_PATHS.stream()
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
