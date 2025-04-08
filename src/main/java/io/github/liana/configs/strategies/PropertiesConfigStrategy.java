package io.github.liana.configs.strategies;

import io.github.liana.configs.ConfigStrategy;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PropertiesConfigStrategy implements ConfigStrategy {

    @Override
    public boolean supports(String fileExtension) {
        return fileExtension.equalsIgnoreCase("properties");
    }

    @Override
    public Configuration loadConfiguration(Path path) {
        try (BufferedReader input = Files.newBufferedReader(path)) {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.read(input);
            return config;
        } catch (IOException | ConfigurationException ex) {
            throw new RuntimeException("Error loading Properties config from " + path, ex);
        }
    }
}
