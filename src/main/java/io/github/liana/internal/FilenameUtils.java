package io.github.liana.internal;

import static java.util.Objects.requireNonNull;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

/**
 * Utility class for working with file path strings. Provides methods to extract the file name,
 * retrieve the file extension, and normalize a path.
 *
 * <p>This class is designed to be null-safe and to handle invalid path input
 * gracefully by converting {@link InvalidPathException} into {@link IllegalArgumentException}.
 *
 * <p>All methods are static and the class is not instantiable.
 */
public final class FilenameUtils {

  private FilenameUtils() {
  }

  /**
   * Returns the name of the file from the given path string.
   *
   * <p>For example, given {@code "config/resource/application.yaml"}, this method returns
   * {@code "application.yaml"}.
   *
   * @param path the file path string
   * @return the file name component of the path
   * @throws NullPointerException     if {@code path} is {@code null}
   * @throws IllegalArgumentException if {@code path} is syntactically invalid
   */
  public static String getName(String path) {
    requireNonNullOrThrow(path);
    return resolvePath(() -> Paths.get(path).getFileName()).toString();
  }

  /**
   * Returns the file extension from the given path string.
   *
   * <p>If the path is blank or does not contain a period ({@code '.'}),
   * an empty string is returned. For example, given {@code "file.tar.gz"}, this method returns
   * {@code "gz"}.
   *
   * @param path the file name or path string
   * @return the extension without the period, or an empty string if none
   * @throws NullPointerException if {@code path} is {@code null}
   */
  public static String getExtension(String path) {
    requireNonNullOrThrow(path);
    if (path.isBlank()) {
      return "";
    }

    int index = path.lastIndexOf(".");
    return index == -1 ? "" : path.substring(index + 1);
  }

  /**
   * Resolves a {@link Path} using the given {@link Supplier}, converting any
   * {@link InvalidPathException} into an {@link IllegalArgumentException}.
   *
   * <p>This method is intended for internal use to safely wrap path operations.
   *
   * @param pathSupplier a supplier of {@link Path} instances
   * @return the resolved {@link Path}
   * @throws IllegalArgumentException if the supplied path is invalid
   */
  public static Path resolvePath(Supplier<Path> pathSupplier) {
    try {
      return pathSupplier.get();
    } catch (InvalidPathException e) {
      throw new IllegalArgumentException("Invalid path", e);
    }
  }

  /**
   * Validates that the provided path is not {@code null}, throwing a {@link NullPointerException}
   * with a clear message if it is.
   *
   * @param path the path string to validate
   * @throws NullPointerException if {@code path} is {@code null}
   */
  private static void requireNonNullOrThrow(String path) {
    requireNonNull(path, "path must not be null");
  }
}
