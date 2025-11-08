package io.github.liana.config;

import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.internal.FilenameUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ResourceNameValidator {

  private final Set<Path> baseDirectories;

  public ResourceNameValidator(Collection<String> baseDirectories) {
    requireNonNull(baseDirectories, "baseDirectories must not be null");
    this.baseDirectories = baseDirectories.stream()
        .filter(Objects::nonNull)
        .map(base -> Paths.get(base).toAbsolutePath().normalize())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public boolean isSafeResourceName(String resourceName) {
    if (isBlank(resourceName)) {
      return false;
    }

    var normalizedInput = resourceName.replace('\\', '/');

    return baseDirectories.stream()
        .anyMatch(base -> isWithinBaseDirectory(base, normalizedInput));
  }

  private boolean isWithinBaseDirectory(Path baseDirectory, String resourceName) {
    try {
      return FilenameUtils.resolvePath(() -> baseDirectory.resolve(resourceName).normalize())
          .startsWith(baseDirectory);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
