package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

final class LoadersRegistry {

  private final List<ConfigLoader> customLoaders;
  private final JacksonMappers jacksonMappers;
  private final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);

  public LoadersRegistry(List<ConfigLoader> customLoaders, JacksonMappers jacksonMappers) {
    this.customLoaders = List.copyOf(
        requireNonNull(customLoaders, "customLoaders must not be null"));
    this.jacksonMappers = requireNonNull(jacksonMappers, "jacksonMappers must not be null");
  }

  public StrategyRegistry<String, ConfigLoader> create() {
    List<ConfigLoader> defaults = List.of(
        new PropertiesLoader(new JacksonParser(jacksonMappers.getProperties())),
        new YamlLoader(new JacksonParser(jacksonMappers.getYaml())),
        new JsonLoader(new JacksonParser(jacksonMappers.getJson())),
        new XmlLoader(new JacksonParser(jacksonMappers.getXml()))
    );

    List<ConfigLoader> merged = Stream.concat(defaults.stream(), customLoaders.stream())
        .toList();

    return new StrategyRegistry<>(keyNormalizer, merged);
  }
}
