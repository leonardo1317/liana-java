package io.github.liana.configs;

import org.apache.commons.configuration2.Configuration;

import java.nio.file.Path;

public interface ConfigStrategy {
    boolean supports(String fileExtension);
    Configuration loadConfiguration(Path path);
}
