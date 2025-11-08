package io.github.liana.config;

import static io.github.liana.config.ConfigDefaults.PROVIDER;
import static io.github.liana.internal.MapUtils.of;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

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

class DefaultConfigResourceLocationBuilder implements ConfigResourceLocationBuilder {

  private static final String RESOURCES_MUST_NOT_BE_NULL = "resources must not be null";
  private static final String VARIABLES_MUST_NOT_BE_NULL = "variables must not be null";
  private static final String PLACEHOLDER_MUST_NOT_BE_NULL = "placeholder must not be null";
  private String provider = "";
  private final Set<String> resourceNames = new LinkedConfigSet();
  private final Set<String> baseDirectories = new LinkedConfigSet();
  private final Map<String, String> variables = new LinkedConfigMap();
  private boolean verboseLogging;
  private Placeholder placeholder;

  /**
   * Sets the provider for this configuration location.
   *
   * @param provider the provider name (e.g., "classpath")
   * @return this builder instance for chaining
   */
  @Override
  public ConfigResourceLocationBuilder provider(String provider) {
    this.provider = provider;

    return this;
  }

  @Override
  public ConfigResourceLocationBuilder baseDirectories(String... baseDirectories) {
    requireNonNull(baseDirectories, "baseDirectories must not be null");
    this.baseDirectories.addAll(Arrays.asList(baseDirectories));
    return this;
  }

  /**
   * Adds a single resource name to the configuration.
   *
   * @param resourceName the resource name to add
   * @return this builder instance for chaining
   */
  @Override
  public ConfigResourceLocationBuilder addResource(String resourceName) {
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
  @Override
  public ConfigResourceLocationBuilder addResources(String... resources) {
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
  @Override
  public ConfigResourceLocationBuilder addResourceFromList(List<String> resources) {
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
  @Override
  public ConfigResourceLocationBuilder addVariable(String key, String value) {
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
  @Override
  public ConfigResourceLocationBuilder addVariables(String... variables) {
    requireNonNull(variables, VARIABLES_MUST_NOT_BE_NULL);

    try {
      return addVariablesFromMap(of(variables));
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
  @Override
  public ConfigResourceLocationBuilder addVariablesFromMap(Map<String, String> variables) {
    requireNonNull(variables, VARIABLES_MUST_NOT_BE_NULL);

    try {
      this.variables.putAll(variables);
    } catch (IllegalArgumentException ex) {
      throw new InvalidConfigVariablesException(ex.getMessage());
    }

    return this;
  }

  /**
   * Enables or disables verbose logging during the configuration loading process.
   *
   * @param verboseLogging {@code true} to enable verbose logging; {@code false} to disable it
   * @return this builder instance for method chaining
   */
  @Override
  public ConfigResourceLocationBuilder verboseLogging(boolean verboseLogging) {
    this.verboseLogging = verboseLogging;

    return this;
  }

  @Override
  public ConfigResourceLocationBuilder placeholders(Placeholder placeholder) {
    requireNonNull(placeholder, PLACEHOLDER_MUST_NOT_BE_NULL);
    this.placeholder = placeholder;

    return this;
  }

  /**
   * Builds the {@link DefaultConfigResourceLocation} instance using the configured parameters.
   *
   * @return a new {@code DefaultConfigResourceLocation} instance
   */
  @Override
  public ConfigResourceLocation build() {
    return new DefaultConfigResourceLocation(
        StringUtils.defaultIfBlank(provider, PROVIDER),
        ImmutableConfigSet.of(baseDirectories),
        ImmutableConfigSet.of(resourceNames),
        ImmutableConfigMap.of(variables),
        verboseLogging,
        requireNonNullElse(placeholder, Placeholders.builder().build())
    );
  }
}
