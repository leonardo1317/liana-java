package io.github.liana.config.core;

import static java.util.Objects.requireNonNull;

/**
 * Lightweight identifier for a resource within a provider.
 *
 * <p>This record represents a composite identifier formed by the provider id and the logical
 * resource name. It is intended as a small, immutable descriptor that can be used as a key to
 * locate or request the actual resource content (which is represented by a separate type).
 *
 * <p>Semantically this is a key-like object (possibly composite), but the name emphasizes its
 * role as an identifier rather than a single primitive key string.
 *
 * @param provider     the provider identifier; must not be {@code null}
 * @param resourceName the logical resource name; must not be {@code null}
 */
public record DefaultResourceIdentifier(String provider, String resourceName) implements
    ResourceIdentifier {

  public DefaultResourceIdentifier {
    requireNonNull(provider, "provider must not be null");
    requireNonNull(resourceName, "resourceName must not be null");
  }
}
