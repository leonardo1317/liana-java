package io.github.liana.config.core;

import io.github.liana.config.api.ResourceLocation;
import java.util.Map;

/**
 * Defines a processing pipeline capable of resolving and loading a resource, merging its content,
 * and applying placeholder interpolation.
 *
 * <p>This interface is part of the internal module API. Implementations are
 * expected to be stateless and thread-safe, but this depends on the provided collaborators.</p>
 */
public interface Pipeline {

  /**
   * Executes the resource-loading pipeline for the given location.
   *
   * @param location the resource location descriptor; must not be null
   * @return a merged and interpolated view of the resource as a map
   * @throws NullPointerException if {@code location} is null
   * @throws RuntimeException     if loaders or providers fail during processing
   */
  Map<String, Object> execute(ResourceLocation location);
}
