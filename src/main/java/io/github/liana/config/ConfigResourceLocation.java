/**
 * Copyright 2025 Leonardo Favio Romero Silva
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p><a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */

package io.github.liana.config;

import static io.github.liana.config.ConfigDefaults.PROVIDER;
import static io.github.liana.internal.MapUtils.toMap;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.InvalidConfigCredentialsException;
import io.github.liana.config.exception.InvalidConfigVariablesException;
import io.github.liana.internal.ImmutableConfigMap;
import io.github.liana.internal.ImmutableConfigSet;
import io.github.liana.internal.LinkedConfigMap;
import io.github.liana.internal.LinkedConfigSet;
import io.github.liana.internal.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a configuration resource location, including its provider, resource names, variables,
 * and credentials.
 *
 * <p>This class is typically built using its nested {@link Builder}, allowing clients to fluently
 * configure where and how to load configuration resources (e.g., from classpath, S3, Azure Blob,
 * etc.).
 *
 * <p>Resource names are maintained in insertion order and de-duplicated. Variables and credentials
 * are stored securely in {@link LinkedConfigMap} instances.
 *
 * <p>Example usage:
 * <pre>{@code
 * ConfigResourceLocation location = ConfigResourceLocation.builder()
 *     .provider("S3")
 *     .addResources("config/app.yaml", "config/app-${evn}.yaml")
 *     .addVariable("env", "dev")
 *     .addCredential("secretKey", "*****")
 *     .build();
 * }</pre>
 */
public class ConfigResourceLocation {

  private static final String RESOURCES_MUST_NOT_BE_NULL = "resources must not be null";
  private static final String VARIABLES_MUST_NOT_BE_NULL = "variables must not be null";
  private static final String CREDENTIALS_MUST_NOT_BE_NULL = "credentials must not be null";

  private final String provider;
  private final ImmutableConfigSet resourceNames;
  private final ImmutableConfigMap variables;
  private final ImmutableConfigMap credentials;
  private final boolean verboseLogging;

  /**
   * Constructs a new {@code ConfigResourceLocation} instance.
   *
   * @param provider      the configuration provider (e.g., "classpath", "S3", "Azure Blob")
   * @param resourceNames an immutable set of resource names
   * @param variables     an immutable map of variables used for configuration
   * @param credentials   an immutable map of credentials used for authentication
   */
  public ConfigResourceLocation(String provider,
      ImmutableConfigSet resourceNames,
      ImmutableConfigMap variables,
      ImmutableConfigMap credentials,
      boolean verboseLogging
  ) {
    this.provider = provider;
    this.resourceNames = resourceNames;
    this.variables = variables;
    this.credentials = credentials;
    this.verboseLogging = verboseLogging;
  }

  /**
   * Gets the provider for the configuration.
   *
   * @return the provider (e.g., "classpath", "S3", "Azure Blob")
   */
  public String getProvider() {
    return provider;
  }

  /**
   * Gets the resource names for the configuration.
   *
   * @return a set of resource names
   */
  public ImmutableConfigSet getResourceNames() {
    return resourceNames;
  }

  /**
   * Gets the credentials associated with the configuration.
   *
   * @return the credentials as a {@link ImmutableConfigMap} instance
   */
  public ImmutableConfigMap getCredentials() {
    return credentials;
  }

  /**
   * Gets the variables associated with the configuration.
   *
   * @return the variables as a {@link ImmutableConfigMap} instance
   */
  public ImmutableConfigMap getVariables() {
    return variables;
  }

  public boolean isVerboseLogging() {
    return verboseLogging;
  }

  /**
   * Returns a new {@link Builder} instance for constructing a {@link ConfigResourceLocation}.
   *
   * @return a new {@link Builder} instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for creating instances of {@link ConfigResourceLocation}. Allows for fluent,
   * step-by-step configuration of resource location details.
   */
  public static class Builder {

    private String provider = "";
    private final Set<String> resourceNames = new LinkedConfigSet();
    private final Map<String, String> variables = new LinkedConfigMap();
    private final Map<String, String> credentials = new LinkedConfigMap();
    private boolean verboseLogging;

    /**
     * Sets the provider for this configuration location.
     *
     * @param provider the provider name (e.g., "classpath", "S3", "Azure Blob")
     * @return this builder instance for chaining
     */
    public Builder provider(String provider) {
      this.provider = provider;

      return this;
    }

    /**
     * Adds a single resource name to the configuration.
     *
     * @param resourceName the resource name to add
     * @return this builder instance for chaining
     */
    public Builder addResource(String resourceName) {
      resourceNames.add(resourceName);
      return this;
    }

    /**
     * Adds multiple resource names to the configuration.
     *
     * @param resources an array of resource names to add
     * @return this builder instance for chaining
     * @throws NullPointerException if {@code resources} is null
     */
    public Builder addResources(String... resources) {
      requireNonNull(resources, RESOURCES_MUST_NOT_BE_NULL);

      return addResourceFromList(Arrays.asList(resources));
    }

    /**
     * Adds multiple resource names to the configuration.
     *
     * @param resources a list of resource names to add
     * @return this builder instance for chaining
     * @throws NullPointerException if {@code resources} is null
     */
    public Builder addResourceFromList(List<String> resources) {
      requireNonNull(resources, RESOURCES_MUST_NOT_BE_NULL);
      resourceNames.addAll(resources);

      return this;
    }

    /**
     * Adds a single variable key-value pair to the configuration.
     *
     * @param key   the variable key
     * @param value the variable value
     * @return this builder instance for chaining
     * @throws InvalidConfigVariablesException if the variable is invalid (e.g., key conflicts)
     */
    public Builder addVariable(String key, String value) {
      try {
        variables.put(key, value);
      } catch (IllegalArgumentException ex) {
        throw new InvalidConfigVariablesException(ex.getMessage());
      }

      return this;
    }

    /**
     * Adds multiple variable key-value pairs to the configuration. The variables are provided as an
     * array of alternating keys and values.
     *
     * @param variables array of alternating keys and values
     * @return this builder instance for chaining
     * @throws NullPointerException            if {@code variables} is null
     * @throws InvalidConfigVariablesException if variables are invalid (e.g., keys conflict)
     */
    public Builder addVariables(String... variables) {
      requireNonNull(variables, VARIABLES_MUST_NOT_BE_NULL);

      try {
        return addVariablesFromMap(toMap(variables));
      } catch (IllegalArgumentException ex) {
        throw new InvalidConfigVariablesException(ex.getMessage());
      }
    }

    /**
     * Adds multiple variable key-value pairs to the configuration.
     *
     * @param variables a map of variables to add
     * @return this builder instance for chaining
     * @throws NullPointerException            if {@code variables} is null
     * @throws InvalidConfigVariablesException if variables are invalid (e.g., keys conflict)
     */
    public Builder addVariablesFromMap(Map<String, String> variables) {
      requireNonNull(variables, VARIABLES_MUST_NOT_BE_NULL);

      try {
        this.variables.putAll(variables);
      } catch (IllegalArgumentException ex) {
        throw new InvalidConfigVariablesException(ex.getMessage());
      }

      return this;
    }

    /**
     * Adds a single credential key-value pair to the configuration.
     *
     * @param key   the credential key
     * @param value the credential value
     * @return this builder instance for chaining
     * @throws InvalidConfigCredentialsException if the credential is invalid (e.g., key conflicts)
     */
    public Builder addCredential(String key, String value) {
      try {
        credentials.put(key, value);
      } catch (IllegalArgumentException ex) {
        throw new InvalidConfigCredentialsException(ex.getMessage());
      }

      return this;
    }

    /**
     * Adds multiple credential key-value pairs to the configuration. The credentials are provided
     * as an array of alternating keys and values.
     *
     * @param credentials array of alternating keys and values
     * @return this builder instance for chaining
     * @throws NullPointerException              if {@code credentials} is null
     * @throws InvalidConfigCredentialsException if credentials are invalid (e.g., keys conflict)
     */
    public Builder addCredentials(String... credentials) {
      requireNonNull(credentials, CREDENTIALS_MUST_NOT_BE_NULL);

      try {
        return addCredentialsFromMap(toMap(credentials));
      } catch (IllegalArgumentException ex) {
        throw new InvalidConfigCredentialsException(ex.getMessage());
      }
    }

    /**
     * Adds multiple credential key-value pairs to the configuration.
     *
     * @param credentials a map of credentials to add
     * @return this builder instance for chaining
     * @throws NullPointerException              if {@code credentials} is null
     * @throws InvalidConfigCredentialsException if credentials are invalid (e.g., keys conflict)
     */
    public Builder addCredentialsFromMap(Map<String, String> credentials) {
      requireNonNull(credentials, CREDENTIALS_MUST_NOT_BE_NULL);

      try {
        this.credentials.putAll(credentials);
      } catch (IllegalArgumentException ex) {
        throw new InvalidConfigCredentialsException(ex.getMessage());
      }

      return this;
    }

    /**
     * Enables or disables verbose logging during the configuration loading process.
     *
     * @param verboseLogging {@code true} to enable verbose logging; {@code false} to disable it
     * @return this builder instance for method chaining
     */
    public Builder verboseLogging(boolean verboseLogging) {
      this.verboseLogging = verboseLogging;

      return this;
    }

    /**
     * Builds the {@link ConfigResourceLocation} instance using the configured parameters.
     *
     * @return a new {@code ConfigResourceLocation} instance
     */
    public ConfigResourceLocation build() {
      return new ConfigResourceLocation(
          StringUtils.defaultIfBlank(provider, PROVIDER),
          ImmutableConfigSet.of(resourceNames),
          ImmutableConfigMap.of(variables),
          ImmutableConfigMap.of(credentials),
          verboseLogging
      );
    }
  }
}
