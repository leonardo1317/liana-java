package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

final class ProvidersRegistry {

  private final List<ConfigProvider> customProviders;
  private final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);

  public ProvidersRegistry(List<ConfigProvider> customProviders) {
    this.customProviders = List.copyOf(
        requireNonNull(customProviders, "customProviders must not be null"));
  }

  public StrategyRegistry<String, ConfigProvider> create(ClasspathResource resource) {
    List<ConfigProvider> defaults = List.of(new ClasspathProvider(resource));

    List<ConfigProvider> merged = Stream.concat(defaults.stream(), customProviders.stream())
        .toList();

    return new StrategyRegistry<>(keyNormalizer, merged);
  }
}
