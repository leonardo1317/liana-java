package io.github.liana.config;

import static io.github.liana.config.ConfigFileFormat.isExtensionForFormat;
import static io.github.liana.internal.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

import io.github.liana.config.exception.ConfigLoaderException;
import io.github.liana.internal.FilenameUtils;
import java.util.List;
import java.util.Map;

class ConfigurationLoader {

  private static final List<ConfigLoader> strategies = List.of(
      new YamlConfigLoader(),
      new PropertiesConfigLoader(),
      new JsonConfigLoader(),
      new XmlConfigLoader()
  );

  private ConfigurationLoader() {
  }

  public static Configuration create(ConfigResource resource) {
    requireNonNull(resource, "ConfigResource cannot be null to create a Configuration");
    String resourceName = requireNonBlank(resource.getResourceName(),
        "resourceName cannot be null or blank to create a Configuration");
    String fileExtension = FilenameUtils.getExtension(resourceName);
    return strategies.stream()
        .filter(configLoader -> isExtensionForFormat(configLoader.getFileFormat(), fileExtension))
        .findFirst()
        .orElseThrow(() -> new ConfigLoaderException("Unsupported config file: " + resourceName))
        .load(resource);
  }

  public static Configuration create(Map<String, Object> map) {
    return new MapConfiguration(map);
  }
}
