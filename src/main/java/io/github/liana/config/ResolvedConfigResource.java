package io.github.liana.config;

import io.github.liana.util.ImmutableConfigMap;

public class ResolvedConfigResource {
    private final String provider;
    private final String resourceName;
    private final ImmutableConfigMap credentials;

    public ResolvedConfigResource(String provider, String resourceName, ImmutableConfigMap credentials) {
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
        return "ResolvedConfigResource{" +
                "provider='" + provider + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", credentials=" + credentials +
                '}';
    }
}
