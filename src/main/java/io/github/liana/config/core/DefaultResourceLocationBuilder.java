package io.github.liana.config.core;

import static io.github.liana.config.core.Constants.PROVIDER;
import static io.github.liana.config.internal.MapUtils.of;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.api.ResourceLocationBuilder;
import io.github.liana.config.core.exception.InvalidVariablesException;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;
import io.github.liana.config.internal.LinkedConfigMap;
import io.github.liana.config.internal.LinkedConfigSet;
import io.github.liana.config.internal.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link ResourceLocationBuilder}.
 *
 * <p>This class provides a fluent API to configure and build {@link DefaultResourceLocation}
 * instances. It performs validation and normalization of resources, variables, and placeholder
 * strategies. Builders are **not thread-safe** and intended for single-threaded construction.
 */
public final class DefaultResourceLocationBuilder implements ResourceLocationBuilder {

  private static final String RESOURCES_MUST_NOT_BE_NULL = "resources must not be null";
  private static final String VARIABLES_MUST_NOT_BE_NULL = "variables must not be null";
  private static final String PLACEHOLDER_MUST_NOT_BE_NULL = "placeholder must not be null";
  private String provider = "";
  private final Set<String> resourceNames = new LinkedConfigSet();
  private final Set<String> baseDirectories = new LinkedConfigSet();
  private final Map<String, String> variables = new LinkedConfigMap();
  private boolean verboseLogging;
  private Placeholder placeholder;

  @Override
  public ResourceLocationBuilder provider(String provider) {
    this.provider = provider;

    return this;
  }

  @Override
  public ResourceLocationBuilder baseDirectories(String... baseDirectories) {
    requireNonNull(baseDirectories, "baseDirectories must not be null");
    this.baseDirectories.addAll(Arrays.asList(baseDirectories));
    return this;
  }

  @Override
  public ResourceLocationBuilder addResource(String resourceName) {
    resourceNames.add(resourceName);

    return this;
  }

  @Override
  public ResourceLocationBuilder addResources(String... resources) {
    requireNonNull(resources, RESOURCES_MUST_NOT_BE_NULL);

    return addResourceFromList(Arrays.asList(resources));
  }

  @Override
  public ResourceLocationBuilder addResourceFromList(List<String> resources) {
    requireNonNull(resources, RESOURCES_MUST_NOT_BE_NULL);
    resourceNames.addAll(resources);

    return this;
  }

  @Override
  public ResourceLocationBuilder addVariable(String key, String value) {
    try {
      variables.put(key, value);
    } catch (IllegalArgumentException ex) {
      throw new InvalidVariablesException(ex.getMessage());
    }

    return this;
  }

  @Override
  public ResourceLocationBuilder addVariables(String... variables) {
    requireNonNull(variables, VARIABLES_MUST_NOT_BE_NULL);

    try {
      return addVariablesFromMap(of(variables));
    } catch (IllegalArgumentException ex) {
      throw new InvalidVariablesException(ex.getMessage());
    }
  }

  @Override
  public ResourceLocationBuilder addVariablesFromMap(Map<String, String> variables) {
    requireNonNull(variables, VARIABLES_MUST_NOT_BE_NULL);

    try {
      this.variables.putAll(variables);
    } catch (IllegalArgumentException ex) {
      throw new InvalidVariablesException(ex.getMessage());
    }

    return this;
  }

  @Override
  public ResourceLocationBuilder verboseLogging(boolean verboseLogging) {
    this.verboseLogging = verboseLogging;

    return this;
  }

  @Override
  public ResourceLocationBuilder placeholders(Placeholder placeholder) {
    requireNonNull(placeholder, PLACEHOLDER_MUST_NOT_BE_NULL);
    this.placeholder = placeholder;

    return this;
  }

  @Override
  public ResourceLocation build() {
    return new DefaultResourceLocation(
        StringUtils.defaultIfBlank(provider, PROVIDER),
        ImmutableConfigSet.of(baseDirectories),
        ImmutableConfigSet.of(resourceNames),
        ImmutableConfigMap.of(variables),
        verboseLogging,
        requireNonNullElse(placeholder, Placeholder.builder().build())
    );
  }
}
