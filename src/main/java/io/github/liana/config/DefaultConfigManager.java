package io.github.liana.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.liana.config.ConfigMerger.merge;

final class DefaultConfigManager implements ConfigManager {
    private final ConfigMapCache<String, Object> cache = new ConfigMapCache<>();

    @Override
    public ConfigReader load(ConfigResourceLocation configResourceLocation) {
        ConcurrentHashMap<String, Object> cachedConfig = cache.get(() -> getConfig(configResourceLocation));
        return new DefaultConfigReader(ConfigLoaderFactory.create(cachedConfig));
    }

    private ConcurrentHashMap<String, Object> getConfig(ConfigResourceLocation configResourceLocation) {
        ConfigResourcePreparer configResourcePreparer = new ConfigResourcePreparer(configResourceLocation);
        List<Map<String, Object>> configs = new ArrayList<>();
        for (ResolvedConfigResource resolvedConfigResource : configResourcePreparer.prepare()) {
            if (!resolvedConfigResource.getProvider().isBlank() && !resolvedConfigResource.getResourceName().isBlank()) {
                ConfigResource configResource = ConfigProviderFactory.create(resolvedConfigResource);
                ConfigWrapper configWrapper = ConfigLoaderFactory.create(configResource);
                Map<String, Object> allSettings = configWrapper.getAllSettings();
                configs.add(allSettings);
            }
        }

        return new ConcurrentHashMap<>(merge(configs));
    }
}
