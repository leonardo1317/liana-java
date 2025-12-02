package io.github.liana.config.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of supported configuration file formats and their associated file extensions.
 *
 * <p>Each enum constant defines a set of valid file extensions. Extensions are normalized
 * to lowercase and returned as an unmodifiable set.
 *
 * <p>This enum is intended to provide a central reference for all recognized configuration
 * file types.
 */
public enum FileFormat {
  PROPERTIES(of("properties")),
  YAML(of("yaml", "yml")),
  JSON(of("json")),
  XML(of("xml"));

  private final Set<String> extensions;

  FileFormat(Set<String> extensions) {
    this.extensions = extensions;
  }

  /**
   * Returns the immutable set of extensions associated with this configuration file format.
   *
   * <p>All extensions are in lowercase and preserve insertion order.
   *
   * @return an unmodifiable set of lowercase extensions
   */
  public Set<String> getExtensions() {
    return extensions;
  }

  private static Set<String> of(String... values) {
    var extensions = Arrays.stream(values)
        .map(extension -> extension.toLowerCase(Locale.ROOT))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    return Collections.unmodifiableSet(extensions);
  }
}
