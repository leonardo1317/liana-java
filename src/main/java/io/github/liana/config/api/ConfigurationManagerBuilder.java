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

import io.github.liana.config.spi.ResourceLoader;
import io.github.liana.config.spi.ResourceProvider;

/**
 * Builds and configures a {@link ConfigurationManager} instance.
 *
 * <p>This builder acts as the assembly mechanism for registering configuration
 * providers and loaders used during configuration resolution. Implementations may apply default
 * components for parts of the system that are not explicitly configured through this API.
 *
 * <p>This interface defines the public API for constructing a configuration manager
 * and does not guarantee thread-safety across builder instances.
 */
public interface ConfigurationManagerBuilder {

  /**
   * Registers one or more {@link ResourceProvider} instances to be used when resolving configuration
   * resources.
   *
   * @param providers the providers to register; must not be {@code null}
   * @return this builder
   */
  ConfigurationManagerBuilder addProviders(ResourceProvider... providers);

  /**
   * Registers one or more {@link ResourceLoader} instances to be used when loading configuration data
   * from resolved locations.
   *
   * @param loaders the loaders to register; must not be {@code null}
   * @return this builder
   */
  ConfigurationManagerBuilder addLoaders(ResourceLoader... loaders);

  /**
   * Builds and returns a new {@link ConfigurationManager} instance using the registered components and any
   * defaults required by the implementation.
   *
   * @return a configured configuration manager; never {@code null}
   */
  ConfigurationManager build();
}
