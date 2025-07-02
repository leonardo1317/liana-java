package io.github.liana.config;

import io.github.liana.internal.ImmutableConfigMap;

public class ConfigResourceReference {

  private final String provider;
  private final String resourceName;
  private final ImmutableConfigMap credentials;

  public ConfigResourceReference(String provider, String resourceName,
      ImmutableConfigMap credentials) {
    this.provider = provider;
    this.resourceName = resourceName;
    this.credentials = credentials;
  }

  public String getProvider() {
    return provider;
  }

  public ImmutableConfigMap getCredentials() {
    return credentials;
  }

  public String getResourceName() {
    return resourceName;
  }

  @Override
  public String toString() {
    return "ConfigResourceReference{"
        + "provider='" + provider + '\''
        + ", resourceName='" + resourceName + '\''
        + ", credentials=" + credentials
        + '}';
  }
}
