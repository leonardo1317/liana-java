package io.github.liana.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.liana.config.ConfigMerger.merge;

final class DefaultConfigManager implements ConfigManager {

    @Override
    public ConfigReader load(ConfigResourceLocation configResourceLocation) {
        ConfigResourcePreparer configResourcePreparer = new ConfigResourcePreparer(configResourceLocation);
        List<Map<String, Object>> configs = new ArrayList<>();
        for (ResolvedConfigResource resolvedConfigResource : configResourcePreparer.prepare()) {
            if (!resolvedConfigResource.getProvider().isBlank() && !resolvedConfigResource.getResourceName().isBlank()) {
                ConfigResource configResource = ConfigProviderFactory.resolveResource(resolvedConfigResource);
                ConfigWrapper configWrapper = ConfigLoaderFactory.fromFile(configResource);
                Map<String, Object> allSettings = configWrapper.getAllSettings();
                configs.add(allSettings);
            }
        }

        Map<String, Object> mergedConfig = merge(configs);
        return new DefaultConfigReader(ConfigLoaderFactory.fromMap(mergedConfig));
    }
}
