package io.github.liana.config;

import io.github.liana.config.exception.ConfigLoaderException;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.github.liana.config.ConfigFileFormat.PROPERTIES;

/**
 * Implementation of {@link ConfigLoader} for Properties configuration files.
 */
final class PropertiesConfigLoader implements ConfigLoader {

    /**
     * Gets the configuration file format supported by this loader.
     * <p>
     * This implementation specifically returns the Properties format.
     *
     * @return The {@link ConfigFileFormat#PROPERTIES} constant representing the Properties format
     * @see ConfigFileFormat#PROPERTIES
     */
    @Override
    public ConfigFileFormat getFileFormat() {
        return PROPERTIES;
    }

    /**
     * Loads and parses a Properties configuration resource.
     *
     * @param resource The configuration resource to load (must not be null).
     * @return A {@link ConfigWrapper} with the parsed configuration.
     * Loads and parses a Properties configuration resource.
     * @throws NullPointerException  If {@code resource} or any of its required fields (input stream, resource name) are null.
     * @throws ConfigLoaderException if the resource is invalid or the Properties is malformed.
     */
    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.read(new InputStreamReader(input, StandardCharsets.UTF_8));
            return new ConfigWrapper(config);
        } catch (IOException | ConfigurationException ex) {
            throw new ConfigLoaderException("Error loading Properties config from " + resource.getResourceName(), ex);
        }
    }
}
