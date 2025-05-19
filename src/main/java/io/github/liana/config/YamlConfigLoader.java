package io.github.liana.config;

import io.github.liana.config.exception.ConfigLoaderException;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;

import static io.github.liana.config.ConfigFileFormat.YAML;

/**
 * Implementation of {@link ConfigLoader} for YAML configuration files.
 */
final class YamlConfigLoader implements ConfigLoader {

    /**
     * Gets the configuration file format supported by this loader.
     * <p>
     * This implementation specifically returns the YAML format, which supports
     * both ".yaml" and ".yml" file extensions.
     *
     * @return The {@link ConfigFileFormat#YAML} constant representing the YAML format
     * @see ConfigFileFormat#YAML
     */
    @Override
    public ConfigFileFormat getFileFormat() {
        return YAML;
    }

    /**
     * Loads and parses an YAML configuration resource.
     *
     * @param resource The configuration resource to load (must not be null).
     * @return A {@link ConfigWrapper} with the parsed configuration.
     * Loads and parses an YAML configuration resource.
     * @throws NullPointerException  If {@code resource} or any of its required fields (input stream, resource name) are null.
     * @throws ConfigLoaderException if the resource is invalid or the YAML is malformed.
     */
    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            YAMLConfiguration config = new YAMLConfiguration();
            config.read(input);
            return new ConfigWrapper(config);
        } catch (ConfigurationException | IOException ex) {
            throw new ConfigLoaderException("Error loading Yaml config from " + resource.getResourceName(), ex);
        }
    }
}
