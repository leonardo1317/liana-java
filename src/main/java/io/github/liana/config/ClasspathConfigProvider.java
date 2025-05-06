package io.github.liana.config;

import java.io.InputStream;

class ClasspathConfigProvider implements ConfigProvider {

    @Override
    public String getProvider() {
        return "classpath";
    }

    @Override
    public ConfigResource resolveResource(ResolvedConfigResource locator) {
        validateSource(locator);
        InputStream input = ClasspathResource.getResourceAsStream(locator.getResourceName());
        if (input == null) {
            throw new RuntimeException("Config resource not found: " + locator.getResourceName());
        }
        return new ConfigResource(locator.getResourceName(), input);
    }
}
