package io.github.liana.config;

import java.util.List;

class ConfigProviderFactory {
    private static final List<ConfigProvider> strategies = List.of(
            new ClasspathConfigProvider()
    );

    private ConfigProviderFactory() {
    }

    public static ConfigResource create(ResolvedConfigResource resource) {
        return strategies.stream()
                .filter(strategy -> strategy.getProvider().equalsIgnoreCase(resource.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No config provider found for provider: " + resource.getProvider()))
                .resolveResource(resource);
    }
}

