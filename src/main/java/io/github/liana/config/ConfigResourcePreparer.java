package io.github.liana.config;

import io.github.liana.util.ImmutableConfigMap;
import io.github.liana.util.ImmutableConfigSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.github.liana.config.ConfigDefaults.BASE_RESOURCE_NAME;
import static io.github.liana.config.ConfigDefaults.BASE_RESOURCE_NAME_PATTERN;
import static io.github.liana.config.ConfigDefaults.PROFILE_ENV_VAR;
import static io.github.liana.config.ConfigDefaults.PROFILE_VAR;
import static io.github.liana.config.ConfigDefaults.PROVIDER;
import static io.github.liana.util.PlaceholderUtils.replaceIfAllPresent;
import static io.github.liana.util.StringUtils.DOT;
import static io.github.liana.util.StringUtils.EMPTY_STRING;
import static io.github.liana.util.StringUtils.defaultIfBlank;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Copyright 2025 Leonardo Favio Romero Silva
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a>
 */
class ConfigResourcePreparer {
    private static final Logger LOGGER = Logger.getLogger(ConfigResourcePreparer.class.getName());
    private final ConfigResourceLocation configResourceLocation;
    private final String profile;

    public ConfigResourcePreparer(ConfigResourceLocation configResourceLocation) {
        this(configResourceLocation, System.getenv(PROFILE_ENV_VAR));
    }

    public ConfigResourcePreparer(ConfigResourceLocation configResourceLocation, String profile) {
        this.configResourceLocation = requireNonNull(configResourceLocation, ConfigResourceLocation.class.getSimpleName() + " must not be null");
        this.profile = defaultIfBlank(profile, "default");
    }

    public List<ResolvedConfigResource> prepare() {
        String preparedProvider = prepareProvider();
        boolean isDefaultProvider = preparedProvider.equalsIgnoreCase(PROVIDER);
        ImmutableConfigMap preparedVariables = prepareVariables(isDefaultProvider);
        List<String> preparedResourceNames = prepareResourceNames(isDefaultProvider, preparedVariables);
        ImmutableConfigMap preparedCredentials = prepareCredentials(isDefaultProvider);

        return preparedResourceNames.stream()
                .map(name -> new ResolvedConfigResource(preparedProvider, name, preparedCredentials))
                .collect(Collectors.toList());
    }

    private String prepareProvider() {
        String providedProvider = requireNonNullElse(configResourceLocation.getProvider(), EMPTY_STRING);

        return providedProvider.isBlank() ? PROVIDER : providedProvider;
    }

    private ImmutableConfigMap prepareVariables(boolean isDefaultProvider) {
        ImmutableConfigMap providedVariables = requireNonNullElse(configResourceLocation.getVariables(), ImmutableConfigMap.empty());

        if (isDefaultProvider && providedVariables.isEmpty()) {
            return ImmutableConfigMap.of(Map.of(PROFILE_VAR, profile));
        }

        return providedVariables;
    }

    private List<String> prepareResourceNames(boolean isDefaultProvider, ImmutableConfigMap immutableConfigMap) {
        ImmutableConfigSet providedResourceNames = requireNonNullElse(configResourceLocation.getResourceNames(), ImmutableConfigSet.empty());

        Map<String, String> variables = immutableConfigMap.toMap();
        if (isDefaultProvider && providedResourceNames.isEmpty()) {
            return processDefaultResourceNames(variables);
        }

        return processProvidedResourceNames(providedResourceNames, variables);
    }

    private List<String> processDefaultResourceNames(Map<String, String> variables) {
        String processedDefaultName = resolveExtension(BASE_RESOURCE_NAME)
                .filter(FilenameValidator::isSafeResourceName)
                .orElse(EMPTY_STRING);

        String processedPattern = replaceIfAllPresent(BASE_RESOURCE_NAME_PATTERN, variables)
                .flatMap(this::resolveExtension)
                .filter(FilenameValidator::isSafeResourceName)
                .orElse(EMPTY_STRING);

        return List.of(processedDefaultName, processedPattern);
    }

    private List<String> processProvidedResourceNames(ImmutableConfigSet resourceNames, Map<String, String> variables) {
        return resourceNames.toSet().stream()
                .map(name -> replaceIfAllPresent(name, variables))
                .flatMap(Optional::stream)
                .filter(FilenameValidator::isSafeResourceName)
                .collect(Collectors.toUnmodifiableList());
    }

    private Optional<String> resolveExtension(String baseResourceName) {
        return ConfigFileFormat.getAllSupportedExtensions().stream()
                .map(extension -> formatResourceName(baseResourceName, extension))
                .filter(ClasspathResource::resourceExists)
                .findFirst()
                .or(() -> {
                    LOGGER.warning("No standard config file found in classpath for base: " + baseResourceName);
                    return Optional.empty();
                });
    }

    private String formatResourceName(String baseResourceName, String extension) {
        return baseResourceName + DOT + extension;
    }

    private ImmutableConfigMap prepareCredentials(boolean isDefaultProvider) {
        ImmutableConfigMap providedCredentials = requireNonNullElse(configResourceLocation.getCredentials(), ImmutableConfigMap.empty());

        if (isDefaultProvider && providedCredentials.isEmpty()) {
            return ImmutableConfigMap.empty();
        }

        return providedCredentials;
    }
}
