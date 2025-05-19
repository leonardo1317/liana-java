package io.github.liana.config;

import io.github.liana.config.exception.ConfigProviderException;

import java.io.InputStream;

/**
 * Provides configuration resources from the application's classpath.
 * <p>
 * This implementation handles resources located in the classpath, typically
 * files stored in {@code src/main/resources} or bundled in JAR files.
 */
final class ClasspathConfigProvider implements ConfigProvider {

    /**
     * Returns the provider identifier for classpath resources.
     *
     * @return The constant string "classpath" identifying this provider type.
     */
    @Override
    public String getProvider() {
        return "classpath";
    }

    /**
     * Resolves a classpath resource into a loadable configuration resource.
     *
     * @param resource The resolved resource descriptor containing the resource name (must not be null)
     * @return A {@link ConfigResource} with an open input stream to the classpath resource
     * @throws NullPointerException    If the resource or its name is null
     * @throws ConfigProviderException If the resource cannot be found in the classpath
     * @implNote The caller is responsible for closing the returned resource's input stream
     */
    @Override
    public ConfigResource resolveResource(ResolvedConfigResource resource) {
        validateResource(resource);
        InputStream input = ClasspathResource.getResourceAsStream(resource.getResourceName());
        if (input == null) {
            throw new ConfigProviderException("Config resource not found: " + resource.getResourceName());
        }
        return new ConfigResource(resource.getResourceName(), input);
    }
}
