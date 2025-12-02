package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.ConfigurationManager;
import io.github.liana.config.api.ConfigurationManagerBuilder;
import io.github.liana.config.providers.ClasspathProvider;
import io.github.liana.config.spi.ResourceLoader;
import io.github.liana.config.spi.ResourceProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for creating fully configured {@link ConfigurationManager} instances.
 *
 * <p>This implementation of {@link ConfigurationManagerBuilder} includes by default:
 * <ul>
 *   <li>Standard configuration loaders: Properties, YAML, JSON, XML</li>
 *   <li>A default {@link ClasspathProvider}</li>
 * </ul>
 *
 * <p>Additional providers or loaders can be registered using
 * {@link #addProviders(ResourceProvider...)} or {@link #addLoaders(ResourceLoader...)}.
 *
 * <p>Instances built by this builder use {@link JacksonMappers} internally to parse and merge
 * configuration data.
 */
public final class DefaultConfigurationManagerBuilder implements ConfigurationManagerBuilder {

  private final JacksonMappers jacksonMappers = JacksonMappers.create();
  private final List<ResourceProvider> providers = new ArrayList<>();
  private final List<ResourceLoader> loaders = new ArrayList<>();

  /**
   * {@inheritDoc}
   *
   * <p>This implementation adds the given providers to an internal list, preserving insertion
   * order. Providers will be included when building the {@link ConfigurationManager}.
   *
   * @param providers one or more {@link ResourceProvider} instances
   * @return this builder instance
   * @throws NullPointerException if {@code providers} is {@code null}
   */
  @Override
  public ConfigurationManagerBuilder addProviders(ResourceProvider... providers) {
    requireNonNull(providers, "providers must not be null");
    this.providers.addAll(Arrays.asList(providers));
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation adds the given loaders to an internal list, preserving insertion
   * order. Loaders will be included when building the {@link ConfigurationManager}.
   *
   * @param loaders one or more {@link ResourceLoader} instances
   * @return this builder instance
   * @throws NullPointerException if {@code loaders} is {@code null}
   */
  @Override
  public ConfigurationManagerBuilder addLoaders(ResourceLoader... loaders) {
    requireNonNull(loaders, "loaders must not be null");
    this.loaders.addAll(Arrays.asList(loaders));
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Builds a {@link DefaultConfigurationManager} using the registered providers and loaders.
   * Internally, {@link JacksonMerger} and {@link JacksonInterpolator} are used for merging and
   * interpolating configuration data.
   *
   * @return a new {@link ConfigurationManager} instance
   */
  @Override
  public ConfigurationManager build() {
    return new DefaultConfigurationManager(
        new DefaultPipeline(new ProvidersRegistry(providers),
            new LoadersRegistry(loaders, jacksonMappers),
            new JacksonMerger(jacksonMappers.getJson()),
            new JacksonInterpolator(jacksonMappers.getJson())
        ));
  }
}
