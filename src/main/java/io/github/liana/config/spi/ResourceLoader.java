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

import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.Configuration;
import io.github.liana.config.core.ResourceStream;
import io.github.liana.config.core.Strategy;
import io.github.liana.config.core.exception.ResourceLoaderException;

/**
 * Loads configuration data from various sources and formats.
 *
 * <p>Implementations handle specific configuration file types such as PROPERTIES, YAML, JSON,
 * XML, or other custom formats. Each loader is responsible for parsing its supported format and
 * returning a {@link Configuration} object.
 *
 * <p>This interface extends {@link Strategy} to allow registration and lookup based on supported
 * file extensions.
 *
 * <p>Implementations must be thread-safe if they are shared across multiple threads.
 */
public interface ResourceLoader extends Strategy<String> {

  /**
   * Loads and parses configuration from the given resource.
   *
   * @param resource the configuration resource to load; must not be {@code null}
   * @return the loaded configuration
   * @throws NullPointerException    if {@code resource} is {@code null}
   * @throws ResourceLoaderException if the resource is invalid, missing, or cannot be parsed
   */
  Configuration load(ResourceStream resource);

  /**
   * Performs basic validation of the provided resource.
   *
   * <p>Default implementation checks that {@code resource}, its input stream, and resource name
   * are not {@code null}. Implementations may override to add additional validation.
   *
   * @param resource the resource to validate; must not be {@code null}
   * @throws NullPointerException if {@code resource} or its required fields are {@code null}
   */
  default void validateResource(ResourceStream resource) {
    requireNonNull(resource, "DefaultResourceStream must not be null");
    requireNonNull(resource.stream(), "stream must not be null");
    requireNonNull(resource.name(), "name must not be null");
  }
}
