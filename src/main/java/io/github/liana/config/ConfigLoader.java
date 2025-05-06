package io.github.liana.config;

import java.util.Set;

import static java.util.Objects.requireNonNull;

interface ConfigLoader {
    Set<String> getExtensions();

    ConfigWrapper load(ConfigResource resource);

    default void validateResource(ConfigResource resource) {
        requireNonNull(resource, ConfigResource.class.getSimpleName() + " must not be null");
        requireNonNull(resource.getInputStream(), "InputStream must not be null");
        String resourceName = requireNonNull(resource.getResourceName(), "ResourceName must not be null");
        if (resourceName.isBlank()) {
            throw new IllegalArgumentException("ResourceName must not be blank");
        }
    }
}
