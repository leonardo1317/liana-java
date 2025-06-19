package io.github.liana.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.liana.internal.MapMerger.merge;
import static io.github.liana.internal.StringUtils.isBlank;
import static java.util.Objects.requireNonNull;

final class DefaultConfigManager implements ConfigManager {
    private static final long NANOS_PER_MILLISECOND = 1_000_000L;
    private static final ConfigMapCache<String, Object> cache = new ConfigMapCache<>();

    @Override
    public ConfigReader load(ConfigResourceLocation location) {
        requireNonNull(location, "ConfigResourceLocation cannot be null when loading configuration");
        Map<String, Object> cachedConfig = cache.get(() -> getConfig(location));

        return new DefaultConfigReader(ConfigurationLoader.create(cachedConfig));
    }

    private Map<String, Object> getConfig(ConfigResourceLocation location) {
        ConfigLogger log = ConsoleConfigLogger.getLogger(location.isVerboseLogging());
        log.debug(() -> "Starting configuration load");
        ConfigResourcePreparer configResourcePreparer = new ConfigResourcePreparer(location);
        List<ConfigResourceReference> references = configResourcePreparer.prepare();
        List<Map<String, Object>> configs = processConfigResources(references, log);

        int total = references.size();
        int loaded = configs.size();
        int failed = total - loaded;
        log.info(() -> String.format("Configuration load completed: loaded=%d, failed=%d (total=%d)",
                loaded, failed, total));

        return merge(configs);
    }

    private List<Map<String, Object>> processConfigResources(List<ConfigResourceReference> references, ConfigLogger log) {
        List<Map<String, Object>> configs = new ArrayList<>();
        for (ConfigResourceReference reference : references) {
            if (isBlank(reference.getProvider()) || isBlank(reference.getResourceName())) {
                log.debug(() -> "Empty provider or resource name");
                continue;
            }

            processSingleConfigResource(reference, log).ifPresent(configs::add);
        }

        return configs;
    }

    private Optional<Map<String, Object>> processSingleConfigResource(ConfigResourceReference reference, ConfigLogger log) {
        log.debug(() -> "Loading resource: " + reference.getResourceName());
        try {
            long startTime = System.nanoTime();
            ConfigResource configResource = ConfigResourceProvider.create(reference);
            Configuration configuration = ConfigurationLoader.create(configResource);
            Map<String, Object> allConfig = configuration.getAllConfig();
            long durationMs = (System.nanoTime() - startTime) / NANOS_PER_MILLISECOND;
            log.debug(() -> String.format("Loaded %s with %d entries in %dms",
                    reference.getResourceName(), allConfig.size(), durationMs));

            return Optional.of(allConfig);
        } catch (Exception ex) {
            log.error(reference::getResourceName, ex);
            return Optional.empty();
        }
    }
}
