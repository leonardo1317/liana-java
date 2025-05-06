package io.github.liana.config;

import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

class JsonConfigLoader implements ConfigLoader {

    @Override
    public Set<String> getExtensions() {
        return Set.of("json");
    }

    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            JSONConfiguration config = new JSONConfiguration();
            config.read(input);
            return new ConfigWrapper(config);
        } catch (ConfigurationException | IOException ex) {
            throw new RuntimeException("Error loading Json config from " + resource.getResourceName(), ex);
        }
    }
}
