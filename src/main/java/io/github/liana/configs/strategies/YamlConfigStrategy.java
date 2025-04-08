package io.github.liana.configs.strategies;

import io.github.liana.configs.ConfigStrategy;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlConfigStrategy implements ConfigStrategy {

    @Override
    public boolean supports(String fileExtension) {
        return fileExtension.equalsIgnoreCase("yml") || fileExtension.equalsIgnoreCase("yaml");
    }

    @Override
    public Configuration loadConfiguration(Path path) {
        System.out.println("path " + path);
        try (InputStream input = Files.newInputStream(path)) {
            YAMLConfiguration config = new YAMLConfiguration();
            config.read(input);
            return config;
        } catch (IOException | ConfigurationException ex) {
            throw new RuntimeException("Error loading YAML config from " + path, ex);
        }
    }
}
