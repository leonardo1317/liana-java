package io.github.liana.config;

import org.apache.commons.io.FilenameUtils;

import java.util.Map;
import java.util.Set;

import static io.github.liana.config.FileExtensionValidator.isValid;

class ConfigLoaderFactory {
    private static final Set<ConfigLoader> strategies = Set.of(
            new YamlConfigLoader(),
            new PropertiesConfigLoader(),
            new JsonConfigLoader(),
            new XmlConfigLoader()
    );

    public ConfigLoaderFactory() {
    }

    public static ConfigWrapper fromFile(ConfigResource resource) {
        String fileExtension = FilenameUtils.getExtension(resource.getResourceName());
        return strategies.stream()
                .filter(strategy -> isValid(strategy.getExtensions(), fileExtension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported config file: " + resource.getResourceName()))
                .load(resource);
    }

    public static ConfigWrapper fromMap(Map<String, Object> map) {
        return new ConfigWrapper(map);
    }
}
