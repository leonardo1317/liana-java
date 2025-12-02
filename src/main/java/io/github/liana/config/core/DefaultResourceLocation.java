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
package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;

/**
 * Default immutable implementation of {@link ResourceLocation}.
 *
 * <p>This class provides a straightforward data holder for the values defined by the
 * {@code ResourceLocation} contract. It performs null-validation on all required parameters and
 * stores the supplied immutable collections and placeholder resolver without applying additional
 * processing or behavioral rules.
 *
 * <p>Instances are fully immutable and therefore thread-safe.
 */
public record DefaultResourceLocation(
    String provider,
    ImmutableConfigSet baseDirectories,
    ImmutableConfigSet resourceNames,
    ImmutableConfigMap variables,
    boolean verboseLogging,
    Placeholder placeholder
) implements ResourceLocation {

  /**
   * Creates a new immutable configuration resource location.
   *
   * @param provider        the provider identifier; must not be {@code null}
   * @param baseDirectories the base directories to search; must not be {@code null}
   * @param resourceNames   the logical resource names to resolve; must not be {@code null}
   * @param variables       the variables for placeholder interpolation; must not be {@code null}
   * @param verboseLogging  whether verbose logging is enabled
   * @param placeholder     the placeholder resolver; must not be {@code null}
   * @throws NullPointerException if any argument except {@code verboseLogging} is {@code null}
   */
  public DefaultResourceLocation {
    requireNonNull(provider, "provider must not be null");
    requireNonNull(baseDirectories, "baseDirectories must not be null");
    requireNonNull(resourceNames, "resourceNames must not be null");
    requireNonNull(variables, "variables must not be null");
    requireNonNull(placeholder, "placeholder must not be null");
  }
}
