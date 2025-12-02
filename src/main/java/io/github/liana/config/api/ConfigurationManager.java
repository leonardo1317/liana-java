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

import io.github.liana.config.core.DefaultConfigurationManagerBuilder;

/**
 * Loads configuration resources from one or more logical locations.
 *
 * <p>This interface defines the contract for retrieving configuration
 * data based on {@link ResourceLocation} descriptors. A location identifies where and how a
 * resource should be resolved, without exposing implementation details.
 */
public interface ConfigurationManager {

  /**
   * Resolves a configuration resource defined by the given location.
   *
   * <p>The returned {@link Configuration} represents the resource as
   * provided by the underlying resolution mechanism. If the resource cannot be resolved,
   * implementations may return {@code null} or provide a fallback depending on their policy.
   *
   * @param location the logical descriptor of the configuration resource
   * @return the resolved {@code DefaultResourceStream}, or {@code null} if not found
   * @throws NullPointerException if {@code location} is {@code null}
   */
  Configuration load(ResourceLocation location);

  /**
   * Returns a new {@link ConfigurationManagerBuilder} for constructing custom configuration managers.
   *
   * @return a new configuration builder
   */
  static ConfigurationManagerBuilder builder() {
    return new DefaultConfigurationManagerBuilder();
  }
}
