package io.github.liana.config;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for creating customized {@link ConfigManager} instances.
 *
 * <p>By default, this builder includes:
 * <ul>
 *   <li>Standard configuration loaders: Properties, YAML, JSON, XML</li>
 *   <li>A default {@link ClasspathProvider}</li>
 * </ul>
 *
 * <p>Additional providers or loaders can be registered using
 * {@link #addProviders(ConfigProvider...)} or {@link #addLoaders(ConfigLoader...)}.
 *
 * <p>Example usage:
 * <pre>{@code
 * ConfigManager manager = new DefaultLianaConfigBuilder()
 *     .addProviders(new HttpProvider())
 *     .addLoaders(new TomlLoader(new JacksonParser(...)))
 *     .build();
 * }</pre>
 */
public final class DefaultLianaConfigBuilder implements LianaConfigBuilder {

  private final JacksonMappers jacksonMappers = JacksonMappers.create();
  private final List<ConfigProvider> providers = new ArrayList<>();
  private final List<ConfigLoader> loaders = new ArrayList<>();

  /**
   * Adds additional configuration providers to this builder.
   *
   * @param providers one or more {@link ConfigProvider} instances
   * @return this builder instance
   */
  @Override
  public LianaConfigBuilder addProviders(ConfigProvider... providers) {
    requireNonNull(providers, "providers must not be null");
    this.providers.addAll(Arrays.asList(providers));
    return this;
  }

  /**
   * Adds additional configuration loaders to this builder.
   *
   * @param loaders one or more {@link ConfigLoader} instances
   * @return this builder instance
   */
  @Override
  public LianaConfigBuilder addLoaders(ConfigLoader... loaders) {
    requireNonNull(loaders, "loaders must not be null");
    this.loaders.addAll(Arrays.asList(loaders));
    return this;
  }

  /**
   * Builds a fully configured {@link ConfigManager} instance.
   *
   * @return a new {@link ConfigManager} with the configured strategies
   */
  @Override
  public ConfigManager build() {
    return new DefaultConfigManager(
        new ProvidersRegistry(providers),
        new LoadersRegistry(loaders, jacksonMappers),
        new JacksonMerger(jacksonMappers.getJson()),
        new JacksonInterpolator(jacksonMappers.getJson())
    );
  }
}
