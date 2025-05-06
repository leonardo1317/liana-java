package io.github.liana.config;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

class XmlConfigLoader implements ConfigLoader {

    @Override
    public Set<String> getExtensions() {
        return Set.of("xml");
    }

    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            XMLConfiguration config = new XMLConfiguration();
            FileHandler handler = new FileHandler(config);
            handler.load(input);
            return new ConfigWrapper(config);
        } catch (ConfigurationException | IOException ex) {
            throw new RuntimeException("Error loading Xml config from " + resource.getResourceName(), ex);
        }
    }
}
