package io.github.liana.config;

import static java.util.Objects.requireNonNull;

public interface ConfigProvider {
    String getProvider();

    ConfigResource resolveResource(ResolvedConfigResource locator);

    default void validateSource(ResolvedConfigResource locator) {
        requireNonNull(locator, "ResolvedConfigResource must not be null");
        String resourceName = requireNonNull(locator.getResourceName(), "ResourceName must not be null");
        if (resourceName.isBlank()) {
            throw new IllegalArgumentException("ResourceName must not be blank");
        }
    }
}
