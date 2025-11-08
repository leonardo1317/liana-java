package io.github.liana.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Supported configuration file formats and their associated extensions.
 */
public enum ConfigFileFormat {
  PROPERTIES(of("properties")),
  YAML(of("yaml", "yml")),
  JSON(of("json")),
  XML(of("xml"));

  private final Set<String> extensions;

  ConfigFileFormat(Set<String> extensions) {
    this.extensions = extensions;
  }

  /**
   * Gets all valid extensions for this format.
   *
   * @return Immutable set of extensions in lowercase
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
