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

import io.github.liana.config.core.exception.InvalidVariablesException;
import java.util.List;
import java.util.Map;

/**
 * Builder for {@link ResourceLocation} instances.
 *
 * <p>This interface defines a fluent API to configure the provider, base directories, resource
 * names, variables, logging preferences, and placeholder strategy before constructing an immutable
 * {@link ResourceLocation}.
 *
 * <p>Implementations are expected to be **used by a single thread** during configuration and
 * produce immutable locations. Builders themselves are **not thread-safe**.
 */
public interface ResourceLocationBuilder {

  /**
   * Sets the configuration provider identifier.
   *
   * @param provider the provider name (e.g., "classpath"); must not be {@code null}
   * @return this builder for chaining
   */
  ResourceLocationBuilder provider(String provider);

  /**
   * Sets the base directories where resources may be located.
   *
   * @param baseDirectories the base directory paths; must not be {@code null}
   * @return this builder for chaining
   */
  ResourceLocationBuilder baseDirectories(String... baseDirectories);

  /**
   * Adds a single resource name to be resolved.
   *
   * @param resourceName the resource name; must not be {@code null}
   * @return this builder for chaining
   */
  ResourceLocationBuilder addResource(String resourceName);

  /**
   * Adds multiple resource names to be resolved.
   *
   * @param resources the resource names; must not be {@code null}
   * @return this builder for chaining
   */
  ResourceLocationBuilder addResources(String... resources);

  /**
   * Adds a list of resource names to be resolved.
   *
   * @param resources the resource names; must not be {@code null}
   * @return this builder for chaining
   */
  ResourceLocationBuilder addResourceFromList(List<String> resources);

  /**
   * Adds a variable binding for placeholder interpolation.
   *
   * @param key   the variable name; must not be {@code null}
   * @param value the variable value; must not be {@code null}
   * @return this builder for chaining
   * @throws InvalidVariablesException if the key or value is invalid
   */
  ResourceLocationBuilder addVariable(String key, String value);

  /**
   * Adds multiple variables from an array of alternating key/value pairs.
   *
   * @param variables the key/value pairs; must not be {@code null}
   * @return this builder for chaining
   * @throws InvalidVariablesException if keys or values are invalid
   */
  ResourceLocationBuilder addVariables(String... variables);

  /**
   * Adds multiple variables from a map.
   *
   * @param variables a map of variable names to values; must not be {@code null}
   * @return this builder for chaining
   * @throws InvalidVariablesException if keys or values are invalid
   */
  ResourceLocationBuilder addVariablesFromMap(Map<String, String> variables);

  /**
   * Enables or disables verbose logging during configuration resolution.
   *
   * @param verboseLogging {@code true} to enable verbose logging; {@code false} to disable
   * @return this builder for chaining
   */
  ResourceLocationBuilder verboseLogging(boolean verboseLogging);

  /**
   * Sets the placeholder strategy to use for variable expansion.
   *
   * @param placeholder the placeholder engine; must not be {@code null}
   * @return this builder for chaining
   */
  ResourceLocationBuilder placeholders(Placeholder placeholder);

  /**
   * Constructs a new immutable {@link ResourceLocation} with the configured settings.
   *
   * <p>If the provider was not set, a default provider will be used. If no placeholder engine was
   * set, a default placeholder implementation is applied.
   *
   * @return a fully configured, immutable {@link ResourceLocation}
   * @throws NullPointerException if mandatory parameters are missing
   */
  ResourceLocation build();
}
