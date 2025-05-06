package io.github.liana.config;

import java.util.Set;

class ConfigProviderFactory {
    private static final Set<ConfigProvider> strategies = Set.of(
            new ClasspathConfigProvider(),
            new GitHubConfigProvider()
    );

    private ConfigProviderFactory() {
    }

    public static ConfigResource resolveResource(ResolvedConfigResource locator) {
        return strategies.stream()
                .filter(strategy -> strategy.getProvider().equalsIgnoreCase(locator.getProvider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No config provider found for provider: " + locator.getProvider()))
                .resolveResource(locator);
    }
}

