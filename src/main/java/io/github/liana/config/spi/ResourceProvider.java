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
package io.github.liana.config.spi;

import static io.github.liana.config.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.core.DefaultResourceIdentifier;
import io.github.liana.config.core.DefaultResourceStream;
import io.github.liana.config.core.ResourceIdentifier;
import io.github.liana.config.core.ResourceStream;
import io.github.liana.config.core.Strategy;
import io.github.liana.config.core.exception.ResourceProviderException;

/**
 * Provides configuration resources from different sources.
 *
 * <p>Implementations handle specific locations such as the filesystem, classpath, or remote URLs.
 * Each provider is responsible for resolving {@link DefaultResourceIdentifier} instances into
 * {@link DefaultResourceStream} objects suitable for loading by {@link ResourceLoader}.
 *
 * <p>This interface extends {@link Strategy} with {@link String} keys. Implementations must
 * return a non-null, unmodifiable set of unique identifiers representing the provider.
 *
 * <p>Implementations are expected to validate resources and may throw exceptions if resolution
 * fails. Providers should be thread-safe if shared across multiple threads.
 */
public interface ResourceProvider extends Strategy<String> {

  /**
   * Resolves a configuration resource identifier into a loadable {@link ResourceStream}.
   *
   * @param resource the resource identifier to resolve; must not be {@code null}
   * @return a resolved {@link ResourceStream} ready for loading
   * @throws NullPointerException    if {@code resource} is {@code null}
   * @throws ResourceProviderException if the resource cannot be resolved or access fails
   */
  ResourceStream resolveResource(ResourceIdentifier resource);

  /**
   * Validates the basic requirements of a configuration resource identifier.
   *
   * <p>Default validation ensures the identifier and its resource name are not null or blank.
   * Implementations may override to add further checks.
   *
   * @param resource the resource identifier to validate; must not be {@code null}
   * @throws NullPointerException      if {@code resource} is {@code null}
   * @throws IllegalArgumentException  if the resource name is null or blank
   */
  default void validateResource(ResourceIdentifier resource) {
    requireNonNull(resource, "resource must not be null");
    requireNonBlank(resource.resourceName(), "resourceName must not be null");
  }
}
