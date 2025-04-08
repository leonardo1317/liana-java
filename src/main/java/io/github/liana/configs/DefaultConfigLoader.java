package io.github.liana.configs;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.List;

public class DefaultConfigLoader implements ConfigLoader {

    private final List<ConfigStrategy> strategies;

    public DefaultConfigLoader(List<ConfigStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public Configuration load(String path) {
        String fileExtension = FilenameUtils.getExtension(path);
        return strategies.stream()
                .filter(strategy -> strategy.supports(fileExtension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported config file: " + path))
                .loadConfiguration(Path.of(path));
    }
}
