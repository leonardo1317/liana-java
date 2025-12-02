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

/**
 * Defines a builder for creating {@link Placeholder} instances.
 *
 * <p>This interface is part of the public API and allows customization of the
 * placeholder syntax, including prefix, suffix, delimiter, and escape character. Implementations
 * are expected to be mutable and therefore not thread-safe unless explicitly stated otherwise.
 *
 * <p>The resulting {@link Placeholder} instances should be considered
 * independent of the builder's internal state after construction.
 */
public interface PlaceholderBuilder {

  /**
   * Sets the prefix used to identify the start of a placeholder expression.
   *
   * @param prefix the prefix string; must not be {@code null}
   * @return this builder instance for chaining
   */
  PlaceholderBuilder prefix(String prefix);

  /**
   * Sets the suffix used to identify the end of a placeholder expression.
   *
   * @param suffix the suffix string; must not be {@code null}
   * @return this builder instance for chaining
   */
  PlaceholderBuilder suffix(String suffix);

  /**
   * Sets the delimiter used to separate a key from its default value.
   *
   * @param delimiter the delimiter string; must not be {@code null}
   * @return this builder instance for chaining
   */
  PlaceholderBuilder delimiter(String delimiter);

  /**
   * Sets the escape character used to prevent placeholder evaluation.
   *
   * @param escapeChar the escape character
   * @return this builder instance for chaining
   */
  PlaceholderBuilder escapeChar(char escapeChar);

  /**
   * Builds a new {@link Placeholder} instance using the configured syntax.
   *
   * <p>The returned instance must not depend on subsequent mutations made to
   * this builder.
   *
   * @return a new {@code Placeholder}
   */
  Placeholder build();
}
