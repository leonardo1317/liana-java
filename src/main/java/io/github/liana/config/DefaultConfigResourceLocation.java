/**
 * Copyright 2025 Leonardo Favio Romero Silva
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p><a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */

package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;

/**
 * Default immutable implementation of {@link ConfigResourceLocation}.
 *
 * <p>This class represents a configuration resource location, including its provider,
 * resource names, variables, and logging preferences.
 *
 * <p>All instances are immutable and thread-safe. Resource names are stored in an
 * {@link ImmutableConfigSet}, ensuring insertion order and de-duplication. Variables and
 *
 * <p>Null values are not permitted for any parameter. Attempts to construct an
 * instance with {@code null} arguments will result in {@link NullPointerException}.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * ConfigResourceLocation location =
 *     new DefaultConfigResourceLocation(
 *         "classpath",
 *         ImmutableConfigSet.of("config/app.yaml", "config/app-${env}.yaml"),
 *         ImmutableConfigMap.of("env", "dev"),
 *         true
 *     );
 * }</pre>
 */
class DefaultConfigResourceLocation implements ConfigResourceLocation {

  private final String provider;
  private final ImmutableConfigSet baseDirectories;
  private final ImmutableConfigSet resourceNames;
  private final ImmutableConfigMap variables;
  private final boolean verboseLogging;
  private final Placeholder placeholder;

  /**
   * Constructs a new {@code DefaultConfigResourceLocation}.
   *
   * @param provider       the configuration provider (e.g., "classpath"); must not be {@code null}
   * @param resourceNames  an immutable set of resource names; must not be {@code null}
   * @param variables      an immutable map of variables used for configuration; must not be
   *                       {@code null}
   * @param verboseLogging whether verbose logging is enabled
   * @param placeholder    the placeholder configuration to use for resolving template values; must
   *                       not be {@code null}
   * @throws NullPointerException if any argument except {@code verboseLogging} is {@code null}
   */
  public DefaultConfigResourceLocation(String provider,
      ImmutableConfigSet baseDirectories,
      ImmutableConfigSet resourceNames,
      ImmutableConfigMap variables,
      boolean verboseLogging,
      Placeholder placeholder
  ) {
    this.provider = requireNonNull(provider, "provider must not be null");
    this.baseDirectories = requireNonNull(baseDirectories, "baseDirectories must not be null");
    this.resourceNames = requireNonNull(resourceNames, "resourceNames must not be null");
    this.variables = requireNonNull(variables, "variables must not be null");
    this.verboseLogging = verboseLogging;
    this.placeholder = requireNonNull(placeholder, "placeholder must not be null");
  }

  /**
   * Returns the provider for the configuration.
   *
   * @return the provider name (e.g., "classpath", "GitHub")
   */
  @Override
  public String getProvider() {
    return provider;
  }

  @Override
  public ImmutableConfigSet getBaseDirectories() {
    return baseDirectories;
  }

  /**
   * Returns the resource names for the configuration.
   *
   * @return an immutable set of resource names
   */
  @Override
  public ImmutableConfigSet getResourceNames() {
    return resourceNames;
  }

  /**
   * Returns the variables associated with the configuration.
   *
   * @return the variables as an {@link ImmutableConfigMap}
   */
  @Override
  public ImmutableConfigMap getVariables() {
    return variables;
  }

  /**
   * Indicates whether verbose logging is enabled for this configuration.
   *
   * @return {@code true} if verbose logging is enabled; {@code false} otherwise
   */
  @Override
  public boolean isVerboseLogging() {
    return verboseLogging;
  }

  /**
   * Returns the placeholder configuration used for resolving variables in
   * resource names or values.
   *
   * @return the {@link Placeholder} instance
   */
  @Override
  public Placeholder getPlaceholder() {
    return placeholder;
  }
}
