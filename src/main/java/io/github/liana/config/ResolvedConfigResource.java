package io.github.liana.config;

public class ResolvedConfigResource {
    private final String provider;
    private final String resourceName;
    private final ConfigMap credentials;

    public ResolvedConfigResource(String provider, String resourceName, ConfigMap credentials) {
        this.provider = provider;
        this.resourceName = resourceName;
        this.credentials = credentials;
    }

    public String getProvider() {
        return provider;
    }

    public ConfigMap getCredentials() {
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
