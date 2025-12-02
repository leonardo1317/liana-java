package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.loaders.JsonLoader;
import io.github.liana.config.loaders.PropertiesLoader;
import io.github.liana.config.loaders.XmlLoader;
import io.github.liana.config.loaders.YamlLoader;
import io.github.liana.config.spi.ResourceLoader;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Registry builder for {@link ResourceLoader} instances.
 *
 * <p>This class provides a centralized way to create a {@link StrategyRegistry} that includes
 * both default configuration loaders (properties, YAML, JSON, XML) and custom loaders provided by
 * the user.
 *
 * <p>It ensures immutability by defensively copying the list of custom loaders. Keys for
 * strategy lookup are normalized to lowercase to enable case-insensitive retrieval.
 *
 * <p>This class is internal and intended for constructing loader registries in a consistent way.
 * Thread-safe as it does not mutate state after construction.
 */
public final class LoadersRegistry {

  private final List<ResourceLoader> customLoaders;
  private final JacksonMappers jacksonMappers;
  private final KeyNormalizer<String> keyNormalizer = key -> key.toLowerCase(Locale.ROOT);

  /**
   * Creates a new registry builder with the given custom loaders and Jackson mappers.
   *
   * @param customLoaders  list of custom loaders to include; must not be {@code null}
   * @param jacksonMappers mappers for Jackson parsing of various formats; must not be {@code null}
   * @throws NullPointerException if {@code customLoaders} or {@code jacksonMappers} are
   *                              {@code null}
   */
  public LoadersRegistry(List<ResourceLoader> customLoaders, JacksonMappers jacksonMappers) {
    this.customLoaders = List.copyOf(
        requireNonNull(customLoaders, "customLoaders must not be null"));
    this.jacksonMappers = requireNonNull(jacksonMappers, "jacksonMappers must not be null");
  }

  /**
   * Creates a {@link StrategyRegistry} combining default loaders with custom loaders.
   *
   * <p>Default loaders include standard configuration formats (properties, YAML, JSON, XML)
   * initialized via the provided {@link JacksonMappers}. Custom loaders are appended after
   * default loaders and can override defaults if keys collide.
   *
   * @return a new {@link StrategyRegistry} containing all loaders
   */
  public StrategyRegistry<String, ResourceLoader> create() {
    List<ResourceLoader> defaults = List.of(
        new PropertiesLoader(new JacksonParser(jacksonMappers.getProperties())),
        new YamlLoader(new JacksonParser(jacksonMappers.getYaml())),
        new JsonLoader(new JacksonParser(jacksonMappers.getJson())),
        new XmlLoader(new JacksonParser(jacksonMappers.getXml()))
    );

    List<ResourceLoader> merged = Stream.concat(defaults.stream(), customLoaders.stream())
        .toList();

    return new StrategyRegistry<>(keyNormalizer, merged);
  }
}
