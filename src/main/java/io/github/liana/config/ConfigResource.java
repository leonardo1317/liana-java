package io.github.liana.config;

import java.io.InputStream;

public class ConfigResource {
    private final String resourceName;
    private final InputStream inputStream;

    public ConfigResource(String resourceName, InputStream inputStream) {
        this.resourceName = resourceName;
        this.inputStream = inputStream;
    }

    public String getResourceName() {
        return resourceName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String toString() {
        return "ConfigResource{" +
                "resourceName='" + resourceName + '\'' +
                ", inputStream=" + inputStream +
                '}';
    }
}
