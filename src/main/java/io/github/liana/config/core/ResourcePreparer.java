/**
 * Copyright 2025 Leonardo Favio Romero Silva
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p><a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */
package io.github.liana.config.core;

import static io.github.liana.config.core.Constants.BASE_RESOURCE_NAME;
import static io.github.liana.config.core.Constants.BASE_RESOURCE_NAME_PATTERN;
import static io.github.liana.config.core.Constants.DEFAULT_PROFILE;
import static io.github.liana.config.core.Constants.PROFILE_ENV_VAR;
import static io.github.liana.config.core.Constants.PROFILE_VAR;
import static io.github.liana.config.core.Constants.PROVIDER;
import static io.github.liana.config.internal.StringUtils.defaultIfBlank;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import io.github.liana.config.api.ResourceLocation;
import io.github.liana.config.api.Placeholder;
import io.github.liana.config.internal.ImmutableConfigMap;
import io.github.liana.config.internal.ImmutableConfigSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for preparing a list of configuration resources based on a given
 * {@link ResourceLocation}.
 *
 * <p>This class resolves configuration files depending on the provider, profile, variables, and
 * resource names. It supports resolving default or custom resources using placeholders and
 * environment-based profiles.
 *
 * <p>Variable substitution is applied only if the resource name contains placeholders. If no
 * placeholders are found in the resource name, the name is returned as-is without modification.
 */
public class ResourcePreparer {

  private static final Pattern PROVIDER_PATTERN = Pattern.compile("^(\\w+):(.+)$");
  private final ResourceLocation location;
  private final String profile;
  private final ResourceNameValidator resourceNameValidator;

  /**
   * Constructs a {@code ResourcePreparer} using the given location and the profile obtained from
   * the environment variable {@code PROFILE_ENV_VAR}.
   *
   * <p>This constructor is a convenience shortcut that automatically reads the profile from the
   * {@link ResourceNameValidator} to locate and validate resources.
   *
   * @param location              the config resource location to resolve
   * @param resourceNameValidator the validator used to verify that resource names and extensions
   *                              are safe
   */
  public ResourcePreparer(ResourceLocation location,
      ResourceNameValidator resourceNameValidator) {
    this(location, PropertySources.fromEnv().get(PROFILE_ENV_VAR),
        resourceNameValidator);
  }

  /**
   * Constructs a {@code ResourcePreparer} using the given location, profile, resource resolver, and
   * filename validator.
   *
   * <p>This constructor provides full control over how configuration resources are resolved and
   * validated.
   *
   * @param location              the config resource location to resolve
   * @param profile               the profile name to use for resolving default resources
   * @param resourceNameValidator the validator used to verify that resource names and extensions
   *                              are safe
   */
  public ResourcePreparer(ResourceLocation location, String profile,
      ResourceNameValidator resourceNameValidator) {
    this.location = requireNonNull(location, "location must not be null");
    this.profile = defaultIfBlank(profile, DEFAULT_PROFILE);
    this.resourceNameValidator = requireNonNull(resourceNameValidator,
        "resourceNameValidator must not be null");
  }

  /**
   * Prepares a list of resolved configuration resources based on the location and profile. It
   * determines the appropriate provider, variables, and resource names to construct the list.
   *
   * @return a list of {@link ResourceIdentifier} that are ready for use
   */
  public List<ResourceIdentifier> prepare() {
    final String provider = defaultIfBlank(location.provider(), PROVIDER);
    final boolean isDefaultProvider = provider.equalsIgnoreCase(PROVIDER);
    final ImmutableConfigMap variables = prepareVariables(isDefaultProvider);
    final List<String> resourceNames = prepareResourceNames(isDefaultProvider, variables);

    return resourceNames.stream()
        .map(resourceName -> getConfigResourceReference(provider, resourceName))
        .toList();
  }

  private ResourceIdentifier getConfigResourceReference(String globalProvider,
      String rawName) {
    Matcher matcher = PROVIDER_PATTERN.matcher(rawName);
    if (matcher.matches()) {
      return new DefaultResourceIdentifier(matcher.group(1), matcher.group(2));
    }

    return new DefaultResourceIdentifier(globalProvider, rawName);
  }

  /**
   * Prepares the variable map used for placeholder replacement in resource names. If using the
   * default provider and no variables are present, a default profile variable is injected.
   *
   * @param isDefaultProvider whether the provider is the default one
   * @return an {@link ImmutableConfigMap} of variables
   */
  private ImmutableConfigMap prepareVariables(boolean isDefaultProvider) {
    ImmutableConfigMap variables = requireNonNullElse(location.variables(),
        ImmutableConfigMap.empty());

    if (isDefaultProvider && variables.isEmpty()) {
      return ImmutableConfigMap.of(Map.of(PROFILE_VAR, profile));
    }

    return variables;
  }

  /**
   * Resolves the resource names based on the provider type and variables. Falls back to default
   * resolution logic if no resource names are provided.
   *
   * @param isDefaultProvider whether the provider is the default one
   * @param variables         the variables used for placeholder substitution
   * @return a list of resolved resource names
   */
  private List<String> prepareResourceNames(boolean isDefaultProvider,
      ImmutableConfigMap variables) {
    ImmutableConfigSet providedResourceNames = requireNonNullElse(location.resourceNames(),
        ImmutableConfigSet.empty());
    Placeholder placeholder = requireNonNullElse(location.placeholder(), Placeholder.builder()
        .build());
    Map<String, String> variableMap = variables.toMap();
    if (isDefaultProvider && providedResourceNames.isEmpty()) {
      return resolveDefaultResources(placeholder, variableMap);
    }

    return resolveCustomResources(providedResourceNames, placeholder, variableMap);
  }

  /**
   * Resolves default resource names using variable substitution when applicable.
   *
   * <p>If the base pattern includes placeholders and all required variables are available, the
   * name is resolved accordingly; otherwise, only static names are used.
   *
   * @param placeholder the {@link Placeholder} definition controlling prefix, suffix, and delimiter
   *                    for variable substitution
   * @param variableMap the map of variables for placeholder resolution
   * @return a list of valid and safe resource names found in the classpath
   */
  private List<String> resolveDefaultResources(Placeholder placeholder,
      Map<String, String> variableMap) {
    List<String> processedNames = new ArrayList<>();
    processedNames.add(BASE_RESOURCE_NAME);
    placeholder.replaceIfAllResolvable(BASE_RESOURCE_NAME_PATTERN, variableMap)
        .ifPresent(processedNames::add);

    return Collections.unmodifiableList(processedNames);
  }

  /**
   * Resolves custom resource names from the given set, applying variable substitution only if
   * placeholders are present.
   *
   * <p>Resource names without placeholders are returned as-is.
   *
   * @param resourceNames the set of custom resource names
   * @param placeholder   the {@link Placeholder} definition controlling prefix, suffix, and
   *                      delimiter for variable substitution
   * @param variableMap   the map of variables for substitution
   * @return a list of valid and safe resource names
   */
  private List<String> resolveCustomResources(ImmutableConfigSet resourceNames,
      Placeholder placeholder,
      Map<String, String> variableMap) {

    return resourceNames.toSet().stream()
        .map(template -> placeholder.replaceIfAllResolvable(template, variableMap))
        .flatMap(Optional::stream)
        .filter(resourceNameValidator::isSafeResourceName)
        .toList();
  }
}
