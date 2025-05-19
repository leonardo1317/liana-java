package io.github.liana.config;

import io.github.liana.config.exception.ConfigLoaderException;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

import java.io.IOException;
import java.io.InputStream;

import static io.github.liana.config.ConfigFileFormat.XML;

/**
 * Implementation of {@link ConfigLoader} for XML configuration files.
 */
final class XmlConfigLoader implements ConfigLoader {

    /**
     * Gets the configuration file format supported by this loader.
     * <p>
     * This implementation specifically returns the XML format.
     *
     * @return The {@link ConfigFileFormat#XML} constant representing the XML format
     * @see ConfigFileFormat#XML
     */
    @Override
    public ConfigFileFormat getFileFormat() {
        return XML;
    }

    /**
     * Loads and parses an XML configuration resource.
     *
     * @param resource The configuration resource to load (must not be null).
     * @return A {@link ConfigWrapper} with the parsed configuration.
     * Loads and parses an XML configuration resource.
     * @throws NullPointerException  If {@code resource} or any of its required fields (input stream, resource name) are null.
     * @throws ConfigLoaderException if the resource is invalid or the XML is malformed.
     */
    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            XMLConfiguration config = new XMLConfiguration();
            FileHandler handler = new FileHandler(config);
            handler.load(input);
            return new ConfigWrapper(config);
        } catch (ConfigurationException | IOException ex) {
            throw new ConfigLoaderException("Error loading Xml config from " + resource.getResourceName(), ex);
        }
    }
}
