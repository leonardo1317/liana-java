package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

import io.github.liana.config.api.ResourceLocation;
import java.util.Map;

/**
 * Default implementation of {@link Pipeline} that orchestrates loading, merging, and interpolating
 * resource content.
 *
 * <p>This class is part of the internal infrastructure layer. It coordinates
 * the registered providers and loaders, delegating the actual work to {@code ResourceProcessor},
 * {@code JacksonMerger}, and {@code JacksonInterpolator}.</p>
 *
 * <p>Instances are immutable and thread-safe under the assumption that the
 * supplied registries, merger, and interpolator are themselves thread-safe.</p>
 */
public class DefaultPipeline implements Pipeline {

  private final ProvidersRegistry providers;
  private final LoadersRegistry loaders;
  private final JacksonMerger merger;
  private final JacksonInterpolator interpolator;

  /**
   * Creates a new {@code DefaultPipeline}.
   *
   * @param providers    registry of providers used during resource resolution
   * @param loaders      registry of loaders for resource fetching
   * @param merger       strategy to merge raw resource fragments
   * @param interpolator placeholder interpolator applied after merging
   * @throws NullPointerException if any argument is null
   */
  public DefaultPipeline(
      ProvidersRegistry providers,
      LoadersRegistry loaders,
      JacksonMerger merger,
      JacksonInterpolator interpolator
  ) {
    this.providers = requireNonNull(providers);
    this.loaders = requireNonNull(loaders);
    this.merger = requireNonNull(merger);
    this.interpolator = requireNonNull(interpolator);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation performs the following steps:</p>
   * <ol>
   *   <li>validates the resource name using {@code ResourceNameValidator},</li>
   *   <li>prepares a processing context with {@code ResourcePreparer},</li>
   *   <li>loads raw fragments via {@code ResourceProcessor},</li>
   *   <li>merges the fragments using {@code JacksonMerger}, and</li>
   *   <li>applies placeholder interpolation using {@code JacksonInterpolator}.</li>
   * </ol>
   *
   * <p>No deep validation of the internal state of {@code ResourceLocation}
   * is performed; it must already obey its own invariants.</p>
   */
  @Override
  public Map<String, Object> execute(ResourceLocation location) {
    requireNonNull(location);
    var validator = new ResourceNameValidator(location.baseDirectories());
    var preparer = new ResourcePreparer(location, validator);
    var processor = new ResourceProcessor(providers, loaders, preparer);
    var raw = processor.load(location);
    var merged = merger.merge(raw);

    return interpolator.interpolate(
        merged,
        location.placeholder(),
        location.variables()
    );
  }
}
