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

import java.util.Set;

/**
 * Defines a generic registration strategy contract.
 *
 * <p>This interface represents a strategy that can be uniquely identified by one or more keys.
 * Implementations are registered and selected based on these keys in a strategy registry or lookup
 * system. It is intended for use in modular and pluggable components where multiple implementations
 * may exist.
 *
 * <p>Implementations must ensure that {@link #getKeys()} returns a consistent, non-null set of
 * identifiers. The returned set should be immutable or treated as immutable to preserve registry
 * integrity.
 *
 * <p>Thread-safety is not enforced by this interface. If strategies are shared between threads,
 * implementations should guarantee safe concurrent access or external synchronization.
 *
 * @param <K> the type of key used to identify this strategy (e.g., {@link String}, {@link Enum}, or
 *            {@link Set} of keys)
 */
public interface Strategy<K> {

  /**
   * Returns the unique key(s) that identify this strategy.
   *
   * <p>These keys are used for registration and lookup in strategy registries. Keys must be
   * non-null and, ideally, immutable. Implementations should ensure that the set reflects all keys
   * that can be used to select this strategy.
   *
   * @return a non-null set of key(s) identifying this strategy
   */
  Set<K> getKeys();
}
