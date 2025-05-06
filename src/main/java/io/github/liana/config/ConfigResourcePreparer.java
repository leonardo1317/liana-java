package io.github.liana.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

import static io.github.liana.config.FileExtensionValidator.defaultExtensions;
import static io.github.liana.config.FilenameValidator.isSafeResourceName;
import static io.github.liana.config.PlaceholderUtils.replaceIfAllPresent;
import static io.github.liana.config.StringUtils.DOT;
import static io.github.liana.config.StringUtils.EMPTY_STRING;
import static io.github.liana.config.StringUtils.defaultIfBlank;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

class ConfigResourcePreparer {
    private static final String DEFAULT_PROVIDER = "classpath";
    private static final String DEFAULT_BASE_RESOURCE_NAME = "application";
    private static final String DEFAULT_PROFILE_VAR = "profile";
    private static final String DEFAULT_BASE_RESOURCE_NAME_PATTERN = String.format("%s-${%s}", DEFAULT_BASE_RESOURCE_NAME, DEFAULT_PROFILE_VAR);
    private static final String LIANA_PROFILE_ENV_VAR = "LIANA_PROFILE";
    private static final Logger LOGGER = Logger.getLogger(ConfigResourcePreparer.class.getName());
    private final ConfigResourceLocation configResourceLocation;
    private final String profile;

    public ConfigResourcePreparer(ConfigResourceLocation configResourceLocation) {
        this(configResourceLocation, System.getenv(LIANA_PROFILE_ENV_VAR));
    }

    public ConfigResourcePreparer(ConfigResourceLocation configResourceLocation, String profile) {
        this.configResourceLocation = requireNonNull(configResourceLocation, ConfigResourceLocation.class.getSimpleName() + " must not be null");
        this.profile = defaultIfBlank(profile, "default");
    }

    public List<ResolvedConfigResource> prepare() {
        String preparedProvider = prepareProvider();
        boolean isDefaultProvider = preparedProvider.equalsIgnoreCase(DEFAULT_PROVIDER);
        String preparedResourceName = prepareResourceName(isDefaultProvider);
        ConfigMap preparedVariables = prepareVariables(isDefaultProvider);
        String preparedResourceNamePattern = prepareResourceNamePattern(isDefaultProvider, preparedVariables);
        ConfigMap preparedCredentials = prepareCredentials(isDefaultProvider);

        List<ResolvedConfigResource> preparedConfigResources = new ArrayList<>();
        preparedConfigResources.add(new ResolvedConfigResource(preparedProvider, preparedResourceName, preparedCredentials));
        preparedConfigResources.add(new ResolvedConfigResource(preparedProvider, preparedResourceNamePattern, preparedCredentials));

        return preparedConfigResources;
    }

    private String prepareProvider() {
        String providedProvider = requireNonNullElse(configResourceLocation.getProvider(), EMPTY_STRING);

        return providedProvider.isBlank() ? DEFAULT_PROVIDER : providedProvider;
    }

    private String prepareResourceName(boolean isDefaultProvider) {
        String preparedResourceName = requireNonNullElse(configResourceLocation.getResourceName(), EMPTY_STRING);

        if (isDefaultProvider && preparedResourceName.isBlank()) {
            preparedResourceName = resolveExtension(DEFAULT_BASE_RESOURCE_NAME).orElse(EMPTY_STRING);
        }

        return isSafeResourceName(preparedResourceName) ? preparedResourceName : EMPTY_STRING;
    }

    private ConfigMap prepareVariables(boolean isDefaultProvider) {
        ConfigMap providedVariables = requireNonNullElse(configResourceLocation.getVariables(), ConfigMap.emptyMap());

        if (isDefaultProvider && providedVariables.isEmpty()) {
            return requireNonNullElse(ConfigMap.of(DEFAULT_PROFILE_VAR, profile), ConfigMap.emptyMap());
        }

        return providedVariables;
    }

    private String prepareResourceNamePattern(boolean isDefaultProvider, ConfigMap preparedVariables) {
        String providedResourceNamePattern = requireNonNullElse(configResourceLocation.getResourceNamePattern(), EMPTY_STRING);

        if (isDefaultProvider && providedResourceNamePattern.isBlank()) {
            return replaceIfAllPresent(DEFAULT_BASE_RESOURCE_NAME_PATTERN, preparedVariables.toMap())
                    .flatMap(this::resolveExtension)
                    .filter(FilenameValidator::isSafeResourceName)
                    .orElse(EMPTY_STRING);
        }

        return replaceIfAllPresent(providedResourceNamePattern, preparedVariables.toMap())
                .filter(FilenameValidator::isSafeResourceName)
                .orElse(EMPTY_STRING);
    }

    private Optional<String> resolveExtension(String baseResourceName) {
        return defaultExtensions().stream()
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

    private ConfigMap prepareCredentials(boolean isDefaultProvider) {
        ConfigMap providedCredentials = requireNonNullElse(configResourceLocation.getCredentials(), ConfigMap.emptyMap());

        if (isDefaultProvider && providedCredentials.isEmpty()) {
            return ConfigMap.emptyMap();
        }

        return providedCredentials;
    }
}
