package io.github.liana.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

class PropertiesConfigLoader implements ConfigLoader {

    @Override
    public Set<String> getExtensions() {
        return Set.of("properties");
    }

    @Override
    public ConfigWrapper load(ConfigResource resource) {
        validateResource(resource);
        try (InputStream input = resource.getInputStream()) {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.read(new InputStreamReader(input, StandardCharsets.UTF_8));
            return new ConfigWrapper(config);
        } catch (IOException | ConfigurationException ex) {
            throw new RuntimeException("Error loading Properties config from " + resource.getResourceName(), ex);
        }
    }
}
