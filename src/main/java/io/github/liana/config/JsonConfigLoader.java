package io.github.liana.config;

import io.github.liana.config.exception.ConfigLoaderException;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;

import static io.github.liana.config.ConfigFileFormat.JSON;

/**
 * Implementation of {@link ConfigLoader} for JSON configuration files.
 */
final class JsonConfigLoader implements ConfigLoader {

    /**
     * Gets the configuration file format supported by this loader.
     * <p>
     * This implementation specifically returns the JSON format.
     *
     * @return The {@link ConfigFileFormat#JSON} constant representing the JSON format
     * @see ConfigFileFormat#JSON
     */
    @Override
    public ConfigFileFormat getFileFormat() {
        return JSON;
    }

    /**
     * Loads and parses an JSON configuration resource.
     *
     * @param resource The configuration resource to load (must not be null).
     * @return A {@link ConfigWrapper} with the parsed configuration.
     * Loads and parses an JSON configuration resource.
     * @throws NullPointerException  If {@code resource} or any of its required fields (input stream, resource name) are null.
     * @throws ConfigLoaderException if the resource is invalid or the JSON is malformed.
     */
    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            JSONConfiguration config = new JSONConfiguration();
            config.read(input);
            return new ConfigWrapper(config);
        } catch (ConfigurationException | IOException ex) {
            throw new ConfigLoaderException("Error loading Json config from " + resource.getResourceName(), ex);
        }
    }
}
