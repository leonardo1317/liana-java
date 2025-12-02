/**
 * Copyright 2025 Leonardo Favio Romero Silva
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.liana.config.api;

import io.github.liana.config.core.DefaultPlaceholderBuilder;
import io.github.liana.config.core.PropertySource;
import io.github.liana.config.core.PropertySources;
import java.util.Map;
import java.util.Optional;

/**
 * Defines a contract for resolving placeholder expressions within text templates.
 *
 * <p>A placeholder implementation expands template variables using one or more
 * {@link PropertySource} instances. Resolution is strict: all placeholders must be resolvable for
 * the operation to succeed. Implementations decide how placeholders are parsed, how conflicts are
 * handled, and how sources are combined during evaluation.
 */
public interface Placeholder {

  /**
   * Resolves all placeholders in the given template.
   *
   * <p>The template is resolved using all configured {@link PropertySource} instances plus any
   * additional sources provided in {@code extraSources}. Resolution succeeds only if <em>all</em>
   * placeholders can be expanded; otherwise, an empty {@code Optional} is returned.
   *
   * @param template     the template containing zero or more placeholders; must not be null
   * @param extraSources optional additional sources to consult during resolution
   * @return an {@code Optional} containing the fully resolved template, or empty if any placeholder
   * cannot be resolved
   */
  Optional<String> replaceIfAllResolvable(String template, PropertySource... extraSources);

  /**
   * Resolves all placeholders in the given template using a map-backed property source.
   *
   * <p>This method behaves identically to
   * {@link #replaceIfAllResolvable(String, PropertySource...)}, but wraps the provided map as a
   * temporary {@link PropertySource}.
   *
   * @param template    the template containing placeholders; must not be null
   * @param extraValues key–value pairs to consider during resolution
   * @return an {@code Optional} containing the resolved template, or empty if resolution fails
   */
  default Optional<String> replaceIfAllResolvable(String template,
      Map<String, String> extraValues) {
    return replaceIfAllResolvable(template, PropertySources.fromMap(extraValues));
  }

  /**
   * Returns a new {@link PlaceholderBuilder} instance used to configure and construct placeholder
   * resolution strategies.
   *
   * <p>The returned builder is a concrete implementation intended to be treated as opaque.
   * Clients should rely only on the {@link PlaceholderBuilder} interface and never on the
   * underlying implementation type.
   *
   * @return a new placeholder builder
   */
  static PlaceholderBuilder builder() {
    return new DefaultPlaceholderBuilder();
  }
}
