package io.github.liana.config.core;

import static io.github.liana.config.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.internal.FilenameUtils;
import io.github.liana.config.internal.ImmutableConfigSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates resource names against configured base directories.
 *
 * <p>This internal utility ensures that a resource name is located within one of the specified
 * base directories, preventing path traversal or access outside allowed locations. Base directories
 * are normalized to absolute paths during construction.
 *
 * <p>Instances are immutable and thread-safe. This class is intended for internal use within
 * the configuration module.
 */
public final class ResourceNameValidator {

  private final Set<Path> baseDirectories;

  /**
   * Constructs a validator using the given base directories.
   *
   * <p>All directories are converted to absolute, normalized paths. Null entries are ignored.
   *
   * @param baseDirectories the collection of base directories to validate against; must not be
   *                        {@code null}
   * @throws NullPointerException if {@code baseDirectories} is {@code null}
   */
  public ResourceNameValidator(ImmutableConfigSet baseDirectories) {
    requireNonNull(baseDirectories, "baseDirectories must not be null");
    this.baseDirectories = baseDirectories.toSet().stream()
        .map(base -> Paths.get(base).toAbsolutePath().normalize())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * Checks whether a resource name is safe within the configured base directories.
   *
   * <p>A resource name is considered safe if it is not blank and resolves to a path within at
   * least one base directory. Path traversal attempts or invalid paths return {@code false}.
   *
   * @param resourceName the resource name to validate
   * @return {@code true} if the resource is valid and within a base directory; {@code false}
   * otherwise
   */
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
