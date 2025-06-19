package io.github.liana.config;

import io.github.liana.config.exception.ConfigLoaderException;

import static java.util.Objects.requireNonNull;

/**
 * Loads configuration files from various sources and formats.
 * Implementations handle specific file formats (PROPERTIES, YAML, JSON, etc.).
 */
interface ConfigLoader {

    /**
     * Supported configuration format for this loader.
     */
    ConfigFileFormat getFileFormat();

    /**
     * Loads and parses configuration from the given resource.
     *
     * @throws ConfigLoaderException if the resource is invalid or format is incorrect
     * @throws NullPointerException  if resource is null
     */
    Configuration load(ConfigResource resource);

    /**
     * Validates basic resource requirements.
     * Default checks: non-null resource, input stream and resource name.
     */
    default void validateResource(ConfigResource resource) {
        requireNonNull(resource, "ConfigResource must not be null");
        requireNonNull(resource.getInputStream(), "InputStream must not be null");
        requireNonNull(resource.getResourceName(), "ResourceNames must not be null");
    }
}
