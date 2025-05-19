package io.github.liana.config;

import io.github.liana.config.exception.ConfigProviderException;

import static java.util.Objects.requireNonNull;

/**
 * Provides configuration resources from different sources.
 * Implementations handle specific locations like filesystem, classpath or remote URLs.
 */
public interface ConfigProvider {

    /**
     * Unique identifier for this provider (e.g., "filesystem", "classpath").
     */
    String getProvider();

    /**
     * Resolves a configuration resource into a loadable format.
     *
     * @throws ConfigProviderException if resource can't be resolved
     * @throws NullPointerException    if resource is null
     */
    ConfigResource resolveResource(ResolvedConfigResource resource);

    /**
     * Validates basic resource requirements.
     * Default checks: non-null resource and resource name.
     */
    default void validateResource(ResolvedConfigResource resource) {
        requireNonNull(resource, ResolvedConfigResource.class.getSimpleName() + " must not be null");
        requireNonNull(resource.getResourceName(), "ResourceNames must not be null");
    }
}
