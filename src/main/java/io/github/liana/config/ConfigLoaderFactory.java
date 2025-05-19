package io.github.liana.config;

import org.apache.commons.io.FilenameUtils;

import java.util.Map;
import java.util.List;

class ConfigLoaderFactory {
    private static final List<ConfigLoader> strategies = List.of(
            new YamlConfigLoader(),
            new PropertiesConfigLoader(),
            new JsonConfigLoader(),
            new XmlConfigLoader()
    );

    public ConfigLoaderFactory() {
    }

    public static ConfigWrapper create(ConfigResource resource) {
        String fileExtension = FilenameUtils.getExtension(resource.getResourceName());
        return strategies.stream()
                .filter(strategy -> ConfigFileFormat.isExtensionForFormat(strategy.getFileFormat(), fileExtension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported config file: " + resource.getResourceName()))
                .load(resource);
    }

    public static ConfigWrapper create(Map<String, Object> map) {
        return new ConfigWrapper(map);
    }
}
