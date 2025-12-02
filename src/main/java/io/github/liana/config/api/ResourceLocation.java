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

import io.github.liana.config.core.DefaultResourceLocationBuilder;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;

/**
 * Describes the location of a configuration resource and the rules required to load it.
 *
 * <p>A {@code ResourceLocation} specifies the provider to use, the base directories
 * to search, the resource names to resolve, variable bindings for interpolation, and the
 * placeholder strategy. Implementations are immutable and thread-safe.
 */
public interface ResourceLocation {

  /**
   * Returns the provider identifier that should handle this location.
   *
   * @return the provider name used to resolve configuration resources
   */
  String provider();

  /**
   * Returns the base directories where providers should search for configuration files.
   *
   * <p>The order is significant and influences provider selection and merge precedence.
   *
   * @return an immutable set of base directory identifiers
   */
  ImmutableConfigSet baseDirectories();

  /**
   * Returns the logical names of the configuration resources to load.
   *
   * <p>Each name may correspond to one or more physical resources depending on the provider.
   *
   * @return an immutable set of resource names
   */
  ImmutableConfigSet resourceNames();

  /**
   * Returns the variable bindings used for placeholder interpolation during loading.
   *
   * @return an immutable map of variable names to values
   */
  ImmutableConfigMap variables();

  /**
   * Indicates whether detailed logging should be enabled while resolving this location.
   *
   * @return {@code true} if verbose loading logs should be emitted
   */
  boolean verboseLogging();

  /**
   * Returns the placeholder engine that should be used when interpolating configuration values.
   *
   * @return the placeholder resolver for this location
   */
  Placeholder placeholder();

  /**
   * Returns a new builder for constructing {@link ResourceLocation} instances.
   *
   * @return a fresh {@link ResourceLocationBuilder}
   */
  static ResourceLocationBuilder builder() {
    return new DefaultResourceLocationBuilder();
  }
}
