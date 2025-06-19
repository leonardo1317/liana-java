package io.github.liana.config;

import io.github.liana.config.exception.ConfigProviderException;

import java.util.List;

import static io.github.liana.internal.StringUtils.equalsIgnoreCase;
import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

class ConfigResourceProvider {
    private static final List<ConfigProvider> strategies = List.of(
            new ClasspathConfigProvider()
    );

    private ConfigResourceProvider() {
    }

    public static ConfigResource create(ConfigResourceReference resource) {
        requireNonNull(resource, "ConfigResourceReference cannot be null to create a ConfigResource");
        String provider = requireNonBlank(resource.getProvider(), "provider cannot be null or blank to create a ConfigResource");

        return strategies.stream()
                .filter(configProvider -> equalsIgnoreCase(configProvider.getProvider(), provider))
                .findFirst()
                .orElseThrow(() -> new ConfigProviderException("No config provider found for provider: " + provider))
                .resolveResource(resource);
    }
}
